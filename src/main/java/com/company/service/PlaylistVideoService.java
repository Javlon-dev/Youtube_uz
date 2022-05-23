package com.company.service;

import com.company.dto.*;
import com.company.entity.PlaylistEntity;
import com.company.entity.PlaylistVideoEntity;
import com.company.entity.VideoEntity;
import com.company.enums.VideoStatus;
import com.company.exception.AppBadRequestException;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.mapper.PlaylistVideoInfoMapper;
import com.company.repository.PlaylistRepository;
import com.company.repository.PlaylistVideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistVideoService {

    private final PlaylistVideoRepository playlistVideoRepository;
    private final PlaylistRepository playlistRepository;
    private final VideoService videoService;
    private final AttachService attachService;


    public PlaylistVideoDTO create(PlaylistVideoDTO dto, String profileId) {
        VideoEntity videoEntity = videoService.getById(dto.getVideoId());

        PlaylistEntity playlistEntity = getPlaylistById(dto.getPlaylistId());


        if (!playlistEntity.getChannel().getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        PlaylistVideoEntity entity = new PlaylistVideoEntity();
        entity.setPlaylistId(playlistEntity.getId());
        entity.setVideoId(videoEntity.getId());
        entity.setOrderNum(dto.getOrderNum());

        try {
            playlistVideoRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", profileId);
            throw new AppBadRequestException("Unique!");
        }

        return toDTOMapper(getByIdMapper(entity.getId().toString()));
    }

    public PlaylistVideoDTO update(UpdateOrderNumDTO dto, String playlistVideoId, String profileId) {
        PlaylistVideoEntity entity = getById(playlistVideoId);

        if (!entity.getPlaylist().getChannel().getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        entity.setOrderNum(dto.getOrderNum());
        entity.setUpdatedDate(LocalDateTime.now());

        playlistVideoRepository.save(entity);

        return toDTOMapper(getByIdMapper(playlistVideoId));
    }

    public Boolean delete(PlaylistVideoIdDTO dto, String profileId) {
        VideoEntity videoEntity = videoService.getById(dto.getVideoId());

        PlaylistEntity playlistEntity = getPlaylistById(dto.getPlaylistId());

        if (!playlistEntity.getChannel().getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        PlaylistVideoEntity entity = getByPlaylistIdAndVideoId(dto.getPlaylistId(), dto.getVideoId());

        playlistVideoRepository.delete(entity);
        return true;
    }

    public List<PlaylistVideoDTO> videosByPlaylistId(String playlistId) {
        PlaylistEntity playlistEntity = getPlaylistById(playlistId);

        List<PlaylistVideoDTO> dtoList = new ArrayList<>();

        List<PlaylistVideoInfoMapper> entityList = playlistVideoRepository
                .findAllByPlaylistId(playlistId,
                        String.valueOf(VideoStatus.PUBLIC));

        entityList.forEach(mapper -> {
            dtoList.add(toDTOMapper(mapper));
        });
        return dtoList;
    }

    public PlaylistVideoDTO get(String playlistVideoId) {
        return toDTOMapper(getByIdMapper(playlistVideoId));
    }

    public PlaylistVideoEntity getByPlaylistIdAndVideoId(String playlistId, String videoId) {
        return playlistVideoRepository
                .findByPlaylistIdAndVideoId(UUID.fromString(playlistId), UUID.fromString(videoId))
                .orElseThrow(() -> {
                    log.warn("Not found playlistId={} videoId={}", playlistId, videoId);
                    return new AppBadRequestException("Not found!");
                });
    }


    public PlaylistEntity getPlaylistById(String id) {
        return playlistRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    throw new ItemNotFoundException("Not found!");
                });
    }

    public PlaylistVideoEntity getById(String id) {
        return playlistVideoRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    throw new ItemNotFoundException("Not found!");
                });

    }

    public PlaylistVideoInfoMapper getByIdMapper(String id) {
        return playlistVideoRepository.findByIdMapper(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    throw new ItemNotFoundException("Not found!");
                });

    }

//    public PlaylistVideoDTO toDTO(PlaylistVideoEntity entity) {
//        PlaylistVideoDTO dto = new PlaylistVideoDTO();
//        dto.setId(entity.getId().toString());
//        dto.setPlaylistId(entity.getPlaylistId().toString());
//
//        VideoEntity videoEntity = entity.getVideo();
//        dto.setVideo(new VideoDTO(videoEntity.getId().toString(),
//                videoEntity.getTitle(),
//                videoEntity.getDescription(),
//                new AttachDTO(videoService.toOpenUrl(entity.getVideoId().toString())),
//                videoEntity.getDuration()));
//
//        dto.setChannel(new ChannelDTO(entity.getVideo().getChannelId().toString(), entity.getVideo().getChannel().getName(),
//                new AttachDTO(attachService.toOpenUrl(entity.getVideo().getChannel().getPhotoId().toString()))));
//
//        dto.setOrderNum(entity.getOrderNum());
//        dto.setCreatedDate(entity.getCreatedDate());
//        dto.setUpdatedDate(entity.getUpdatedDate());
//        return dto;
//    }

    public PlaylistVideoDTO toDTOMapper(PlaylistVideoInfoMapper mapper) {
        PlaylistVideoDTO dto = new PlaylistVideoDTO();
        dto.setId(mapper.getPv_id());
        dto.setPlaylistId(mapper.getPl_id());

        new VideoDTO(mapper.getV_id(),
                mapper.getV_title(),
                new AttachDTO(attachService.toOpenUrl(mapper.getV_preview_photo())),
                mapper.getV_duration());

        VideoDTO videoDTO = new VideoDTO();
        videoDTO.setId(mapper.getV_id());
        videoDTO.setTitle(mapper.getV_title());
        videoDTO.setDuration(mapper.getV_duration());
        if (Optional.ofNullable(mapper.getV_preview_photo()).isPresent()) {
            videoDTO.setPreviewPhoto(new AttachDTO(attachService.toOpenUrl(mapper.getV_preview_photo())));
        }
        dto.setVideo(videoDTO);

        ChannelDTO channelDTO = new ChannelDTO();
        channelDTO.setId(mapper.getCh_id());
        channelDTO.setName(mapper.getCh_name());
        if (Optional.ofNullable(mapper.getCh_photo()).isPresent()) {
            channelDTO.setPhoto(new AttachDTO(attachService.toOpenUrl(mapper.getCh_photo())));
        }
        dto.setChannel(channelDTO);

        dto.setOrderNum(mapper.getPv_order_num());
        dto.setCreatedDate(mapper.getPv_created_date());
        return dto;
    }


}
