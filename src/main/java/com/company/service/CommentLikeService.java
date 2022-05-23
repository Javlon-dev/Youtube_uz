package com.company.service;

import com.company.dto.CommentLikeDTO;
import com.company.entity.CommentEntity;
import com.company.entity.CommentLikeEntity;
import com.company.entity.ProfileEntity;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.mapper.CommentLikeInfoMapper;
import com.company.mapper.LikeCountSimpleMapper;
import com.company.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final ProfileService profileService;
    private final CommentService commentService;


    public CommentLikeDTO create(CommentLikeDTO dto, String profileId) {
        ProfileEntity profileEntity = profileService.getById(profileId);

        CommentEntity commentEntity = commentService.getById(dto.getCommentId());

        Optional<CommentLikeEntity> oldLikeOptional = commentLikeRepository
                .findByCommentIdAndProfileId(commentEntity.getId(), profileEntity.getId());

        if (oldLikeOptional.isPresent()) {
            CommentLikeEntity entity = oldLikeOptional.get();
            entity.setType(dto.getType());
            commentLikeRepository.save(entity);
            return toDTOMapper(getByIdMapper(entity.getId().toString()));
        }

        CommentLikeEntity entity = new CommentLikeEntity();
        entity.setCommentId(commentEntity.getId());
        entity.setProfileId(profileEntity.getId());
        entity.setType(dto.getType());

        commentLikeRepository.save(entity);

        return toDTOMapper(getByIdMapper(entity.getId().toString()));
    }

    public Boolean delete(String commentLikeId, String profileId) {
        CommentLikeEntity entity = getById(commentLikeId);

        if (!entity.getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        commentLikeRepository.delete(entity);
        return true;
    }

    public PageImpl<CommentLikeDTO> getByProfileLikedComment(int page, int size, String profileId) {
        ProfileEntity profileEntity = profileService.getById(profileId);

        Pageable pageable = PageRequest.of(page, size);

        List<CommentLikeDTO> dtoList = new ArrayList<>();

        Page<CommentLikeInfoMapper> entityPage = commentLikeRepository
                .findAllByProfileIdMapper(profileEntity.getId(), pageable);

        entityPage.forEach(entity -> {
            dtoList.add(toDTOMapper(entity));
        });
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }


    public CommentLikeEntity getById(String id) {
        return commentLikeRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not found!");
                });
    }

    public CommentLikeInfoMapper getByIdMapper(String id) {
        return commentLikeRepository.findByIdMapper(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not found!");
                });
    }

    public CommentLikeDTO toDTOMapper(CommentLikeInfoMapper mapper) {
        CommentLikeDTO dto = new CommentLikeDTO();
        dto.setId(mapper.getCl_id().toString());
        dto.setType(mapper.getCl_type());
        dto.setCreatedDate(mapper.getCl_created_date());

        dto.setCommentId(mapper.getC_id().toString());

        dto.setProfileId(mapper.getP_id().toString());

        return dto;
    }
}
