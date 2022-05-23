package com.company.repository;

import com.company.entity.VideoLikeEntity;
import com.company.mapper.LikeCountSimpleMapper;
import com.company.mapper.ProfileLikesSimpleMapper;
import com.company.mapper.VideoLikeInfoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoLikeRepository extends JpaRepository<VideoLikeEntity, UUID> {

    Page<VideoLikeEntity> findAllByVideoId(UUID videoId, Pageable pageable);

    Page<VideoLikeEntity> findAllByProfileId(UUID profileId, Pageable pageable);

    Optional<VideoLikeEntity> findByVideoIdAndProfileId(UUID videoId, UUID profileId);

    @Query(value = "select sum(case when type = 'LIKE' THEN 1 else 0 END) like_count," +
            "sum(case when type = 'LIKE' THEN 0 else 1 END) dislike_count " +
            "from video_like " +
            "where video_id = :videoId", nativeQuery = true)
    LikeCountSimpleMapper getLikeCountByVideoId(@Param("videoId") UUID videoId);

    @Query(value = "select CAST(profile_id as varchar) profile_id,type " +
            "from video_like " +
            "where video_id = :videoId " +
            "group by type, profile_id", nativeQuery = true)
    List<ProfileLikesSimpleMapper> getProfileLikesByVideoId(@Param("videoId") UUID videoId);

    @Query(value = "select vl.id as vl_id," +
            "v.id as v_id, v.title as v_title, v.duration as v_duration, v.previewPhotoId as v_preview_photo," +
            "ch.id as ch_id, ch.name as ch_name " +
            "from VideoLikeEntity vl " +
            "inner join vl.video v " +
            "inner join v.channel ch " +
            "where vl.id = :id " +
            "and v.visible = true " +
            "and ch.visible = true ")
    Optional<VideoLikeInfoMapper> findByIdMapper(@Param("id") UUID id);

    @Query(value = "select vl.id as vl_id," +
            "v.id as v_id, v.title as v_title, v.duration as v_duration, v.previewPhotoId as v_preview_photo," +
            "ch.id as ch_id, ch.name as ch_name " +
            "from VideoLikeEntity vl " +
            "inner join vl.video v " +
            "inner join v.channel ch " +
            "inner join vl.profile p " +
            "where vl.profileId = :profileId " +
            "and v.visible = true " +
            "and ch.visible = true " +
            "and p.visible = true " +
            "order by vl.createdDate desc ")
    Page<VideoLikeInfoMapper> findAllByProfileIdMapper(@Param("profileId") UUID profileId, Pageable pageable);
}