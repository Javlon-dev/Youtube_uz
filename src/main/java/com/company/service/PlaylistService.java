package com.company.service;

import com.company.dto.*;
import com.company.entity.ChannelEntity;
import com.company.entity.PlaylistEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.PlaylistStatus;
import com.company.enums.ProfileRole;
import com.company.exception.AppBadRequestException;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.mapper.PlaylistInfoMapper;
import com.company.repository.PlaylistRepository;
import com.company.repository.PlaylistVideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final ChannelService channelService;
    private final ProfileService profileService;
    private final PlaylistVideoRepository playlistVideoRepository;
    private final VideoService videoService;
    private final AttachService attachService;


    public PlaylistDTO create(PlaylistDTO dto, String channelId, String profileId) {
        ChannelEntity channelEntity = channelService.getById(channelId);

        if (!channelEntity.getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        PlaylistEntity entity = new PlaylistEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(PlaylistStatus.PUBLIC);
        entity.setChannelId(channelEntity.getId());
        entity.setOrderNum(dto.getOrderNum());

        try {
            playlistRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique!");
        }
        return toFullDTO(entity);
    }

    public PlaylistDTO updateAbout(PlaylistAboutDTO dto, String playlistId, String profileId) {
        PlaylistEntity entity = getById(playlistId);

        if (!entity.getChannel().getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setOrderNum(dto.getOrderNum());
        entity.setUpdatedDate(LocalDateTime.now());
        try {
            playlistRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique!");
        }
        return toFullDTO(entity);
    }


    public PageImpl<PlaylistDTO> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<PlaylistDTO> dtoList = new ArrayList<>();

        Page<PlaylistInfoMapper> entityPage = playlistRepository.getPlaylistInfo(pageable);

        entityPage.forEach(mapper -> {
            dtoList.add(toDTOMapper(mapper));
        });
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public Boolean changeStatus(String playlistId, String profileId) {
        PlaylistEntity entity = getById(playlistId);

        ProfileEntity profileEntity = profileService.getById(profileId);

        if (entity.getChannel().getProfileId().toString().equals(profileId) ||
                profileEntity.getRole().equals(ProfileRole.ADMIN)) {

            switch (entity.getStatus()) {
                case PUBLIC -> {
                    playlistRepository.updateStatus(PlaylistStatus.PRIVATE, entity.getId());
                }
                case PRIVATE -> {
                    playlistRepository.updateStatus(PlaylistStatus.PUBLIC, entity.getId());
                }
            }

            return true;
        }
        log.warn("Not access {}", profileId);
        throw new AppForbiddenException("Not access!");
    }

    public List<PlaylistDTO> channelPlaylists(String channelId) {
        ChannelEntity channelEntity = channelService.getById(channelId);

        List<PlaylistDTO> dtoList = new ArrayList<>();

        List<PlaylistInfoMapper> entityList = playlistRepository.findAllByChannelIdAndStatus(channelEntity.getId(),
                PlaylistStatus.PUBLIC);

        entityList.forEach(mapper -> {
            dtoList.add(toDTOMapper(mapper));
        });
        return dtoList;
    }

    public List<PlaylistDTO> profilePlaylist(String profileId) {
        ProfileEntity profileEntity = profileService.getById(profileId);

        List<PlaylistDTO> dtoList = new ArrayList<>();

        List<PlaylistInfoMapper> entityList = playlistRepository.findAllByProfileId(profileEntity.getId());

        entityList.forEach(mapper -> {
            dtoList.add(toShortDTOMapper(mapper));
        });
        return dtoList;
    }

    public Boolean delete(String playlistId, String profileId) {
        PlaylistEntity entity = getById(playlistId);

        if (!entity.getChannel().getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        playlistRepository.updateVisible(entity.getId());

        return true;
    }

    public PlaylistDTO get(String playlistId) {
        PlaylistEntity entity = getById(playlistId);
        return toFullDTO(entity);
    }

    public PlaylistEntity getById(String id) {
        return playlistRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    throw new ItemNotFoundException("Not found!");
                });
    }

//    public PlaylistDTO toShortDTO(PlaylistEntity entity) {
//        PlaylistDTO dto = new PlaylistDTO();
//        dto.setId(entity.getId().toString());
//        dto.setName(entity.getName());
//
//        ChannelDTO ChannelDTO = new ChannelDTO(channelService.toOpenUrl(entity.getChannelId().toString()));
//        dto.setChannel(ChannelDTO);
//
//        dto.setVideoCount(playlistVideoRepository.getVideoCountByPlaylistId(entity.getId()));
//
//
//        List<VideoDTO> videoList = playlistVideoRepository.getTop2VideoByPlaylistId(entity.getId())
//                .stream()
//                .map(playlistVideoEntity -> {
//                    VideoDTO videoDTO = new VideoDTO();
//                    videoDTO.setId(playlistVideoEntity.getVideoId().toString());
//                    videoDTO.setTitle(playlistVideoEntity.getVideo().getTitle());
//                    videoDTO.setUrl(videoService.toOpenUrl(playlistVideoEntity.getVideoId().toString()));
//                    videoDTO.setDuration(playlistVideoEntity.getVideo().getDuration());
//                    return videoDTO;
//                }).toList();
//
//        dto.setVideoList(videoList);
//        dto.setCreatedDate(entity.getCreatedDate());
//        return dto;
//    }

    public PlaylistDTO toShortDTOMapper(PlaylistInfoMapper mapper) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(mapper.getPl_id().toString());
        dto.setName(mapper.getPl_name());

        ChannelDTO ChannelDTO = new ChannelDTO(channelService.toOpenUrl(mapper.getCh_id().toString()));
        dto.setChannel(ChannelDTO);

        dto.setVideoCount(playlistVideoRepository.getVideoCountByPlaylistId(mapper.getPl_id()));


        List<VideoDTO> videoList = playlistVideoRepository.getTop2VideoByPlaylistId(mapper.getPl_id())
                .stream()
                .map(videoEntity -> {
                    VideoDTO videoDTO = new VideoDTO();
                    videoDTO.setId(videoEntity.getV_id());
                    videoDTO.setTitle(videoEntity.getV_title());
                    videoDTO.setDuration(videoEntity.getV_duration());
                    return videoDTO;
                }).toList();

        dto.setVideoList(videoList);
        dto.setCreatedDate(mapper.getPl_created_date());
        return dto;
    }

    public PlaylistDTO toFullDTO(PlaylistEntity entity) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(entity.getId().toString());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());

        ChannelDTO ChannelDTO = new ChannelDTO(channelService.toOpenUrl(entity.getChannelId().toString()));
        dto.setChannel(ChannelDTO);

        dto.setStatus(entity.getStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        return dto;
    }

    public PlaylistDTO toDTOMapper(PlaylistInfoMapper mapper) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(mapper.getPl_id().toString());
        dto.setName(mapper.getPl_name());
        dto.setDescription(mapper.getPl_description());

        ChannelDTO channelDTO = new ChannelDTO();
        channelDTO.setId(mapper.getCh_id().toString());
        channelDTO.setName(mapper.getCh_name());
        if (Optional.ofNullable(mapper.getCh_photo_id()).isPresent()) {
            AttachDTO attachDTO = new AttachDTO(attachService.toOpenUrl(mapper.getCh_photo_id().toString()));
            channelDTO.setPhoto(attachDTO);
        }

        dto.setChannel(channelDTO);

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(mapper.getPr_id().toString());
        profileDTO.setName(mapper.getPr_name());
        profileDTO.setSurname(mapper.getPr_surname());

        if (Optional.ofNullable(mapper.getPr_photo_id()).isPresent()) {
            AttachDTO attachDTO = new AttachDTO(attachService.toOpenUrl(mapper.getPr_photo_id().toString()));
            profileDTO.setImage(attachDTO);
        }
        channelDTO.setProfile(profileDTO);
        return dto;
    }
}
