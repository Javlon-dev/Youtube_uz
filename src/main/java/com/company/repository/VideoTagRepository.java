package com.company.repository;

import com.company.entity.VideoTagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoTagRepository extends JpaRepository<VideoTagEntity, UUID> {

    List<VideoTagEntity> findAllByVideoId(UUID videoId);

    Optional<VideoTagEntity> findByVideoIdAndTagId(UUID videoId, UUID tagId);

}