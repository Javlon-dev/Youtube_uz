package com.company.service;

import com.company.dto.*;
import com.company.entity.*;
import com.company.enums.ProfileRole;
import com.company.enums.VideoStatus;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.mapper.*;
import com.company.repository.PlaylistRepository;
import com.company.repository.PlaylistVideoRepository;
import com.company.repository.VideoLikeRepository;
import com.company.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final PlaylistVideoRepository playlistVideoRepository;
    private final PlaylistRepository playlistRepository;
    private final ChannelService channelService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final AttachService attachService;
    private final ProfileService profileService;
    private final VideoLikeRepository videoLikeRepository;


    @Value("${server.domain.name}")
    private String domainName;


    public VideoDTO create(VideoDTO dto, String profileId) {
        ChannelEntity channelEntity = channelService.getById(dto.getChannelId());

        if (!channelEntity.getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        CategoryEntity categoryEntity = categoryService.getById(dto.getCategoryId());

        AttachEntity attachEntity = attachService.getById(dto.getVideoId());

        VideoEntity entity = new VideoEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());

        entity.setStatus(VideoStatus.CREATED);
        entity.setType(dto.getType());

        entity.setCategoryId(categoryEntity.getId());
        entity.setVideoId(attachEntity.getId());
        entity.setChannelId(channelEntity.getId());

        videoRepository.save(entity);

        return toFullDTOMapper(getByIdMapper(entity.getId().toString()));
    }

    public VideoDTO updateAbout(VideoAboutDTO dto, String videoId, String profileId) {
        VideoEntity entity = getById(videoId);

        if (!entity.getChannel().getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setUpdatedDate(LocalDateTime.now());

        videoRepository.save(entity);

        return toFullDTOMapper(getByIdMapper(entity.getId().toString()));
    }

    public Boolean changeStatus(String videoId, String profileId) {
        VideoEntity entity = getById(videoId);

        ProfileEntity profileEntity = profileService.getById(profileId);

        if (entity.getChannel().getProfileId().toString().equals(profileId)
                || profileEntity.getRole().equals(ProfileRole.ADMIN)) {

            switch (entity.getStatus()) {
                case CREATED -> {
                    videoRepository.updateStatusAndPublishedDate(VideoStatus.PUBLIC, LocalDateTime.now(), entity.getId());
                }
                case PUBLIC -> {
                    videoRepository.updateStatus(VideoStatus.PRIVATE, entity.getId());
                }
                case PRIVATE -> {
                    videoRepository.updateStatus(VideoStatus.PUBLIC, entity.getId());
                }
            }
            return true;
        }
        log.warn("Not access {}", profileId);
        throw new AppForbiddenException("Not access!");
    }

    public List<VideoDTO> searchResult(String search) {
        List<VideoDTO> dtoList = new ArrayList<>();

        List<VideoShortInfoMapper> entityList = videoRepository.findAllByTitleAndStatusAndVisible(search,
                VideoStatus.PUBLIC);

        entityList.forEach(entity -> {
            dtoList.add(toShortDTOMapper(entity));
        });
        return dtoList;
    }

    public Boolean updatePreviewPhoto(VideoPreviewPhotoDTO dto, String videoId, String profileId) {
        AttachEntity attachEntity = attachService.getById(dto.getPhotoId());

        VideoEntity entity = getById(videoId);

        if (!entity.getChannel().getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        if (Optional.ofNullable(entity.getPreviewPhotoId()).isPresent()) {
            if (entity.getPreviewPhotoId().toString().equals(dto.getPhotoId())) {
                return true;
            }
            String oldAttach = entity.getPreviewPhotoId().toString();
            videoRepository.updatePreviewPhoto(attachEntity.getId(), entity.getId());
            attachService.delete(oldAttach);
            return true;
        }
        videoRepository.updatePreviewPhoto(attachEntity.getId(), entity.getId());
        return true;
    }

    public void updateViewCount(String videoId) {
        VideoEntity entity = getByIdAndStatus(videoId, VideoStatus.PUBLIC);
        videoRepository.updateViewCount(entity.getId());
    }

    public String updateShareCount(String videoId) {
        VideoEntity entity = getByIdAndStatus(videoId, VideoStatus.PUBLIC);
        videoRepository.updateShareCount(entity.getId());
        return domainName + "video/public/" + videoId;
    }

    public PageImpl<VideoDTO> paginationByCategoryId(int page, int size, String categoryId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedDate"));

        CategoryEntity categoryEntity = categoryService.getById(categoryId);

        List<VideoDTO> dtoList = new ArrayList<>();

        Page<VideoShortInfoMapper> entityPage = videoRepository.findAllByCategoryIdAndStatusAndVisible(categoryEntity.getId(),
                VideoStatus.PUBLIC,
                pageable);

        entityPage.forEach(entity -> {
            dtoList.add(toShortDTOMapper(entity));
        });
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }


    public PageImpl<VideoDTO> paginationByTagId(int page, int size, String tagId) {
        TagEntity tagEntity = tagService.get(tagId);

        Pageable pageable = PageRequest.of(page, size);

        List<VideoDTO> dtoList = new ArrayList<>();

        Page<VideoShortInfoMapper> mapperPage = videoRepository.findAllByTagIdAndStatusAndVisible(tagEntity.getId(),
                VideoStatus.PUBLIC,
                pageable);

        mapperPage.forEach(mapper -> {
            dtoList.add(toShortDTOMapper(mapper));
        });
        return new PageImpl<>(dtoList, pageable, mapperPage.getTotalElements());
    }

    public PageImpl<VideoPlaylistDTO> pagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<VideoDTO> dtoVideoList = new ArrayList<>();
        List<PlaylistDTO> dtoPlaylist = new ArrayList<>();
        List<VideoPlaylistDTO> dtoVideoPlaylist = new ArrayList<>();

        Page<VideoShortInfoMapper> entityPage = videoRepository.findAllByIdMapper(pageable);

        entityPage.forEach(mapper -> {
            PlaylistVideoEntity playlistVideoEntity = getByVideoId(mapper.getV_id());
            if (Optional.ofNullable(playlistVideoEntity).isPresent()) {
                dtoPlaylist.add(toShortPlaylistDTO(playlistRepository.findAllByPlaylistId(playlistVideoEntity.getPlaylistId())));
            }
            dtoVideoList.add(toShortDTOMapper(mapper));
        });
        dtoVideoPlaylist.add(new VideoPlaylistDTO(dtoVideoList, dtoPlaylist));

        return new PageImpl<>(dtoVideoPlaylist, pageable, entityPage.getTotalElements());
    }

    public PageImpl<VideoDTO> paginationByChannelId(int page, int size, String channelId) {
        ChannelEntity channelEntity = channelService.getById(channelId);

        Pageable pageable = PageRequest.of(page, size);

        List<VideoDTO> dtoList = new ArrayList<>();

        Page<VideoShortInfoMapper> entityPage = videoRepository.findAllByChannelIdAndStatusAndVisible(channelEntity.getId(),
                VideoStatus.PUBLIC,
                pageable);

        entityPage.forEach(entity -> {
            dtoList.add(toShortDTOMapper(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public Boolean delete(String videoId, String profileId) {
        VideoEntity entity = getById(videoId);

        if (!entity.getChannel().getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        videoRepository.updateVisible(entity.getId());

        attachService.delete(entity.getVideoId().toString());

        if (Optional.ofNullable(entity.getPreviewPhotoId()).isPresent()) {
            attachService.delete(entity.getPreviewPhotoId().toString());
        }

        return true;
    }

    public VideoDTO get(String id, String profileId) {
        VideoEntity entity = getById(id);

        ProfileEntity profileEntity = profileService.getById(profileId);

        if (!entity.getChannel().getProfileId().toString().equals(profileId)
                && entity.getStatus().equals(VideoStatus.PUBLIC)) {
            return toFullDTOMapper(getByIdMapper(entity.getId().toString()));
        }

        if (entity.getChannel().getProfileId().toString().equals(profileId)
                || profileEntity.getRole().equals(ProfileRole.ADMIN)) {
            return toFullDTOMapper(getByIdMapper(entity.getId().toString()));
        }

        log.warn("Not access {}", profileId);
        throw new AppForbiddenException("Not access!");
    }

    public VideoEntity getById(String id) {
        return videoRepository.findByIdAndVisible(UUID.fromString(id), true)
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    throw new ItemNotFoundException("Not found!");
                });
    }

    public VideoEntity getByIdAndStatus(String id, VideoStatus status) {
        return videoRepository.findByIdAndStatusAndVisible(UUID.fromString(id), status, true)
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    throw new ItemNotFoundException("Not found!");
                });
    }

    public PlaylistVideoEntity getByVideoId(UUID videoId) {
        return playlistVideoRepository
                .findByVideoId(videoId)
                .orElse(null);
    }

    public PlaylistDTO toShortPlaylistDTO(PlaylistInfoMapper mapper) {
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

    public VideoDTO toShortDTO(VideoEntity entity) {
        VideoDTO dto = new VideoDTO();
        dto.setId(entity.getId().toString());
        dto.setTitle(entity.getTitle());

        dto.setViewCount(entity.getViewCount());

        dto.setChannel(new ChannelDTO(channelService.toOpenUrl(entity.getChannelId().toString())));

        dto.setVideo(new AttachDTO(attachService.toOpenUrl(entity.getVideoId().toString())));

        if (Optional.ofNullable(entity.getPreviewPhotoId()).isPresent()) {
            dto.setPreviewPhoto(new AttachDTO(attachService.toOpenUrl(entity.getPreviewPhotoId().toString())));
        }

        dto.setDuration(entity.getDuration());
        dto.setPublishedDate(dto.getPublishedDate());
        return dto;
    }

    public VideoDTO toShortDTOMapper(VideoShortInfoMapper mapper) {
        VideoDTO dto = new VideoDTO();
        dto.setId(mapper.getV_id().toString());
        dto.setTitle(mapper.getV_title());

        dto.setViewCount(mapper.getV_view_count());

        ChannelDTO channelDTO = new ChannelDTO();
        channelDTO.setId(mapper.getCh_id().toString());
        channelDTO.setName(mapper.getCh_name());
        if (Optional.ofNullable(mapper.getCh_photo()).isPresent()) {
            channelDTO.setPhoto(new AttachDTO(attachService.toOpenUrl(mapper.getCh_photo().toString())));
        }
        dto.setChannel(channelDTO);


        if (Optional.ofNullable(mapper.getV_preview_photo()).isPresent()) {
            dto.setPreviewPhoto(new AttachDTO(attachService.toOpenUrl(mapper.getV_preview_photo().toString())));
        }

        dto.setDuration(mapper.getV_duration());
        dto.setPublishedDate(mapper.getV_published_date());
        return dto;
    }

    public VideoDTO toFullDTO(VideoEntity entity) {
        VideoDTO dto = new VideoDTO();
        dto.setId(entity.getId().toString());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());

        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setSharedCount(entity.getSharedCount());
        dto.setViewCount(entity.getViewCount());

        dto.setChannel(new ChannelDTO(channelService.toOpenUrl(entity.getChannelId().toString())));

        dto.setVideo(new AttachDTO(attachService.toOpenUrl(entity.getVideoId().toString())));

        if (Optional.ofNullable(entity.getPreviewPhotoId()).isPresent()) {
            dto.setPreviewPhoto(new AttachDTO(attachService.toOpenUrl(entity.getPreviewPhotoId().toString())));
        }

        dto.setCategory(new CategoryDTO(categoryService.toOpenUrl(entity.getCategoryId().toString())));

        dto.setLikes(getLikesCountByVideoId(entity.getId()));
        dto.setProfileLikes(getProfileLikesByVideoId(entity.getId()));

        dto.setDuration(entity.getDuration());
        dto.setPublishedDate(dto.getPublishedDate());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        return dto;
    }

    public VideoDTO toFullDTOMapper(VideoFullInfoMapper mapper) {
        VideoDTO dto = new VideoDTO();
        dto.setId(mapper.getV_id().toString());
        dto.setTitle(mapper.getV_title());
        dto.setDescription(mapper.getV_description());

        dto.setType(mapper.getV_type());
        dto.setStatus(mapper.getV_status());
        dto.setSharedCount(mapper.getV_shared_count());
        dto.setViewCount(mapper.getV_view_count());

        ChannelDTO channelDTO = new ChannelDTO();
        channelDTO.setId(mapper.getCh_id().toString());
        channelDTO.setName(mapper.getCh_name());
        if (Optional.ofNullable(mapper.getCh_photo()).isPresent()) {
            channelDTO.setPhoto(new AttachDTO(attachService.toOpenUrl(mapper.getCh_photo().toString())));
        }
        dto.setChannel(channelDTO);

        if (Optional.ofNullable(mapper.getV_preview_photo()).isPresent()) {
            dto.setPreviewPhoto(new AttachDTO(attachService.toOpenUrl(mapper.getV_preview_photo().toString())));
        }


        dto.setCategory(new CategoryDTO(mapper.getC_id().toString(), mapper.getC_name()));

        dto.setLikes(getLikesCountByVideoId(mapper.getV_id()));
        dto.setProfileLikes(getProfileLikesByVideoId(mapper.getV_id()));

        dto.setDuration(mapper.getV_duration());
        dto.setPublishedDate(mapper.getV_published_date());
        return dto;
    }

    public VideoFullInfoMapper getByIdMapper(String id) {
        return videoRepository.findByIdMapper(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    throw new ItemNotFoundException("Not found!");
                });
    }

    public VideoLikeDTO getLikesCountByVideoId(UUID videoId) {
        LikeCountSimpleMapper mapper = videoLikeRepository.getLikeCountByVideoId(videoId);
        return new VideoLikeDTO(mapper.getLike_count(), mapper.getDislike_count());
    }

    public List<VideoLikeDTO> getProfileLikesByVideoId(UUID videoId) {
        List<ProfileLikesSimpleMapper> mapper = videoLikeRepository.getProfileLikesByVideoId(videoId);

        List<VideoLikeDTO> dtoList = new ArrayList<>();

        mapper.forEach(entity -> {
            dtoList.add(new VideoLikeDTO(new ProfileDTO(profileService.toOpenUrl(entity.getProfile_id())), entity.getType()));
        });
        return dtoList;
    }

    public String toOpenUrl(String id) {
        return domainName + "video/public/" + id;
    }

}
