package com.company.service;

import com.company.dto.*;
import com.company.entity.CommentEntity;
import com.company.entity.ProfileEntity;
import com.company.entity.VideoEntity;
import com.company.enums.ProfileRole;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.mapper.CommentAdminInfoMapper;
import com.company.mapper.CommentInfoMapper;
import com.company.mapper.LikeCountSimpleMapper;
import com.company.repository.CommentLikeRepository;
import com.company.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ProfileService profileService;
    private final VideoService videoService;
    private final AttachService attachService;


    public CommentDTO create(CommentDTO dto, String profileId) {
        VideoEntity videoEntity = videoService.getById(dto.getVideoId());

        CommentEntity entity = new CommentEntity();
        entity.setProfileId(UUID.fromString(profileId));
        entity.setVideoId(videoEntity.getId());
        entity.setContent(dto.getContent());

        commentRepository.save(entity);
        return toDTOProfileMapper(getByIdMapper(entity.getId().toString()));
    }

    public CommentDTO createReplyId(CommentReplyDTO dto, String profileId) {
        CommentEntity commentEntity = getById(dto.getCommentId());

        CommentEntity entity = new CommentEntity();
        entity.setProfileId(UUID.fromString(profileId));
        entity.setVideoId(commentEntity.getVideoId());
        entity.setContent(dto.getContent());

        commentRepository.save(entity);
        commentRepository.updateReplyId(commentEntity.getId(),entity.getId());

        return toDTOProfileMapper(getByIdMapper(entity.getId().toString()));
    }

    public CommentDTO updateContent(CommentContentDTO dto, String commentId, String profileId) {
        CommentEntity entity = getById(commentId);

        if (!entity.getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        commentRepository.updateContent(dto.getContent(), LocalDateTime.now(), entity.getId());
        return toDTOProfileMapper(getByIdMapper(entity.getId().toString()));
    }

    public Boolean delete(String commentId, String profileId) {
        ProfileEntity profileEntity = profileService.getById(profileId);

        CommentEntity entity = getById(commentId);

        if (entity.getProfileId().toString().equals(profileId) ||
                profileEntity.getRole().equals(ProfileRole.ADMIN)) {
            commentRepository.delete(entity);
            return true;
        }
        log.warn("Not access {}", profileId);
        throw new AppForbiddenException("Not access!");
    }

    public PageImpl<CommentDTO> pagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<CommentDTO> dtoList = new ArrayList<>();
        Page<CommentInfoMapper> entityPage = commentRepository.findAllComments(pageable);

        entityPage.forEach(mapper -> {
            dtoList.add(toDTOProfileMapper(mapper));
        });
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public PageImpl<CommentDTO> paginationByProfileId(int page, int size, String profileId) {
        ProfileEntity profileEntity = profileService.getById(profileId);

        Pageable pageable = PageRequest.of(page, size);

        List<CommentDTO> dtoList = new ArrayList<>();
        Page<CommentAdminInfoMapper> entityPage = commentRepository.findAllByProfileId(profileEntity.getId(), pageable);

        entityPage.forEach(mapper -> {
            dtoList.add(toDTOVideoMapper(mapper));
        });
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public PageImpl<CommentDTO> paginationByVideoId(int page, int size, String videoId) {
        VideoEntity videoEntity = videoService.getById(videoId);

        Pageable pageable = PageRequest.of(page, size);

        List<CommentDTO> dtoList = new ArrayList<>();
        Page<CommentInfoMapper> entityPage = commentRepository.findAllByVideoId(videoEntity.getId(), pageable);

        entityPage.forEach(mapper -> {
            dtoList.add(toDTOProfileMapper(mapper));
        });
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public PageImpl<CommentDTO> paginationReplyByCommentId(int page, int size, String commentId) {
        CommentEntity commentEntity = getById(commentId);

        Pageable pageable = PageRequest.of(page, size);

        List<CommentDTO> dtoList = new ArrayList<>();
        Page<CommentInfoMapper> entityPage = commentRepository.findAllByReplyId(commentEntity.getId(), pageable);

        entityPage.forEach(mapper -> {
            dtoList.add(toDTOProfileMapper(mapper));
        });
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public CommentDTO get(String id) {
        return toDTOProfileMapper(getByIdMapper(id));
    }

    public CommentEntity getById(String id) {
        return commentRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not found!");
                });
    }

    public LikeCountSimpleMapper getLikeCountByCommentId(UUID commentId){
        return commentLikeRepository.getLikeCountByCommentId(commentId);
    }

    public CommentInfoMapper getByIdMapper(String id) {
        return commentRepository.findByIdMapper(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not found!");
                });
    }

    public CommentDTO toDTOVideoMapper(CommentAdminInfoMapper mapper) {
        CommentDTO dto = new CommentDTO();
        dto.setId(mapper.getC_id().toString());
        dto.setContent(mapper.getC_content());

        LikeCountSimpleMapper likeMapper = getLikeCountByCommentId(mapper.getC_id());
        dto.setLikeCount(likeMapper.getLike_count());
        dto.setDislikeCount(likeMapper.getDislike_count());

        VideoDTO videoDTO = new VideoDTO();
        videoDTO.setId(mapper.getV_id().toString());
        videoDTO.setTitle(mapper.getV_title());
        videoDTO.setDuration(mapper.getV_duration());
        videoDTO.setDescription(mapper.getV_description());

        if (Optional.ofNullable(mapper.getV_preview_photo()).isPresent()) {
            videoDTO.setPreviewPhoto(new AttachDTO(attachService.toOpenUrl(mapper.getV_preview_photo().toString())));
        }
        dto.setVideo(videoDTO);

        dto.setCreatedDate(mapper.getC_created_date());
        return dto;
    }

    public CommentDTO toDTOProfileMapper(CommentInfoMapper mapper) {
        CommentDTO dto = new CommentDTO();
        dto.setId(mapper.getC_id().toString());
        dto.setContent(mapper.getC_content());

        LikeCountSimpleMapper likeMapper = getLikeCountByCommentId(mapper.getC_id());
        dto.setLikeCount(likeMapper.getLike_count());
        dto.setDislikeCount(likeMapper.getDislike_count());


        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(mapper.getP_id().toString());
        profileDTO.setName(mapper.getP_name());
        profileDTO.setSurname(mapper.getP_surname());
        if (Optional.ofNullable(mapper.getP_photo()).isPresent()) {
            profileDTO.setImage(new AttachDTO(attachService.toOpenUrl(mapper.getP_photo().toString())));
        }
        dto.setProfile(profileDTO);

        dto.setCreatedDate(mapper.getC_created_date());
        return dto;
    }
}
