package com.company.service;

import com.company.dto.TagDTO;
import com.company.dto.VideoTagDTO;
import com.company.entity.ProfileEntity;
import com.company.entity.TagEntity;
import com.company.entity.VideoEntity;
import com.company.entity.VideoTagEntity;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.VideoTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoTagService {

    private final VideoTagRepository videoTagRepository;
    private final VideoService videoService;
    private final TagService tagService;


    public VideoTagDTO add(VideoTagDTO dto, String profileId) {
        VideoEntity videoEntity = videoService.getById(dto.getVideoId());

        TagEntity tagEntity = tagService.get(dto.getTagId());

        if (!videoEntity.getChannel().getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }
        VideoTagEntity entity = new VideoTagEntity();
        entity.setVideoId(videoEntity.getId());
        entity.setTagId(tagEntity.getId());

        videoTagRepository.save(entity);
        entity.setTag(tagEntity);
        return toDTO(entity);
    }

    public Boolean delete(VideoTagDTO dto, String profileId) {
        VideoEntity videoEntity = videoService.getById(dto.getVideoId());

        TagEntity tagEntity = tagService.get(dto.getTagId());


        if (!videoEntity.getChannel().getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }
        VideoTagEntity entity = get(videoEntity.getId(), tagEntity.getId());

        videoTagRepository.delete(entity);
        return true;
    }

    public List<VideoTagDTO> getAllByVideoId(String videoId) {
        VideoEntity entity = videoService.getById(videoId);

        List<VideoTagDTO> dtoList = new ArrayList<>();
        List<VideoTagEntity> entityList = videoTagRepository.findAllByVideoId(entity.getId());

        entityList.forEach(e -> {
            dtoList.add(toDTO(e));
        });
        return dtoList;
    }

    public VideoTagDTO toDTO(VideoTagEntity entity) {
        VideoTagDTO dto = new VideoTagDTO();
        dto.setId(entity.getId().toString());
        dto.setVideoId(entity.getVideoId().toString());
        dto.setTag(new TagDTO(entity.getTag().getName(), tagService.toSearchUrl(entity.getTagId().toString())));
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public VideoTagEntity get(UUID videoId, UUID tagId) {
        return videoTagRepository.findByVideoIdAndTagId(videoId, tagId)
                .orElseThrow(() -> {
                    log.warn("Not found videoId={} tagId={}", videoId, tagId);
                    throw new ItemNotFoundException("Not found!");
                });
    }
}
