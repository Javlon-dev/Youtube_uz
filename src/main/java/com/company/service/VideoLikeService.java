package com.company.service;

import com.company.dto.*;
import com.company.entity.ProfileEntity;
import com.company.entity.VideoEntity;
import com.company.entity.VideoLikeEntity;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.mapper.VideoLikeInfoMapper;
import com.company.repository.VideoLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoLikeService {

    private final VideoLikeRepository videoLikeRepository;
    private final ProfileService profileService;
    private final VideoService videoService;


    public VideoLikeDTO create(VideoLikeDTO dto, String profileId) {
        ProfileEntity profileEntity = profileService.getById(profileId);

        VideoEntity videoEntity = videoService.getById(dto.getVideoId());

        Optional<VideoLikeEntity> oldLikeOptional = videoLikeRepository.findByVideoIdAndProfileId(videoEntity.getId(), profileEntity.getId());

        if (oldLikeOptional.isPresent()) {
            VideoLikeEntity entity = oldLikeOptional.get();
            entity.setType(dto.getType());
            videoLikeRepository.save(entity);
            return toDTOMapper(getByIdMapper(entity.getId().toString()));
        }

        VideoLikeEntity entity = new VideoLikeEntity();
        entity.setVideoId(videoEntity.getId());
        entity.setProfileId(profileEntity.getId());
        entity.setType(dto.getType());

        videoLikeRepository.save(entity);

        return toDTOMapper(getByIdMapper(entity.getId().toString()));
    }

    public Boolean delete(String likeId, String profileId) {
        VideoLikeEntity entity = getById(likeId);

        if (!entity.getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        videoLikeRepository.delete(entity);
        return true;
    }

    public PageImpl<VideoLikeDTO> getByProfileLikedVideo(int page, int size, String profileId) {
        ProfileEntity profileEntity = profileService.getById(profileId);

        Pageable pageable = PageRequest.of(page, size);

        List<VideoLikeDTO> dtoList = new ArrayList<>();

        Page<VideoLikeInfoMapper> entityPage = videoLikeRepository.findAllByProfileIdMapper(profileEntity.getId(), pageable);

        entityPage.forEach(entity -> {
            dtoList.add(toDTOMapper(entity));
        });
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public VideoLikeDTO get(String videoId, String profileId) {
        ProfileEntity profileEntity = profileService.getById(profileId);

        VideoEntity videoEntity = videoService.getById(videoId);

        Optional<VideoLikeEntity> optional = videoLikeRepository.findByVideoIdAndProfileId(videoEntity.getId(), profileEntity
                .getId());

        if (optional.isEmpty()) {
            return new VideoLikeDTO();
        }
        return toDTOMapper(getByIdMapper(optional.get().getId().toString()));
    }


    public VideoLikeEntity getById(String id) {
        return videoLikeRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not found!");
                });
    }

    public VideoLikeInfoMapper getByIdMapper(String id) {
        return videoLikeRepository.findByIdMapper(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not found!");
                });
    }

    public VideoLikeDTO toDTO(VideoLikeEntity entity) {
        VideoLikeDTO dto = new VideoLikeDTO();
        dto.setId(entity.getId().toString());
        dto.setVideo(new VideoDTO(videoService.toOpenUrl(entity.getVideoId().toString())));
        dto.setProfile(new ProfileDTO(profileService.toOpenUrl(entity.getProfileId().toString())));
        dto.setType(entity.getType());
        return dto;
    }

    public VideoLikeDTO toDTOMapper(VideoLikeInfoMapper mapper) {
        VideoLikeDTO dto = new VideoLikeDTO();
        dto.setId(mapper.getVl_id().toString());

        VideoDTO videoDTO = new VideoDTO();
        videoDTO.setId(mapper.getV_id().toString());
        videoDTO.setTitle(mapper.getV_title());
        videoDTO.setDuration(mapper.getV_duration());
        if (Optional.ofNullable(mapper.getV_preview_photo()).isPresent()){
            videoDTO.setPreviewPhoto(new AttachDTO(videoService.toOpenUrl(mapper.getV_id().toString())));
        }
        dto.setVideo(videoDTO);

        ChannelDTO channelDTO = new ChannelDTO();
        channelDTO.setId(mapper.getCh_id().toString());
        channelDTO.setName(mapper.getCh_name());

        dto.setChannel(channelDTO);

        return dto;
    }
}
