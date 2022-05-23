package com.company.repository;

import com.company.entity.VideoEntity;
import com.company.enums.VideoStatus;
import com.company.mapper.VideoFullInfoMapper;
import com.company.mapper.VideoShortInfoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoRepository extends JpaRepository<VideoEntity, UUID> {


    @Transactional
    @Modifying
    @Query("update VideoEntity set status = :status where id = :id")
    void updateStatus(@Param("status") VideoStatus status, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update VideoEntity set status = :status, publishedDate = :publishedDate where id = :id")
    void updateStatusAndPublishedDate(@Param("status") VideoStatus status,
                                      @Param("publishedDate") LocalDateTime publishedDate,
                                      @Param("id") UUID id);

    @Query(value = "select v.id as v_id,v.title as v_title,v.previewPhotoId as v_preview_photo," +
            "v.publishedDate as v_published_date,v.viewCount as v_view_count, v.duration as v_duration," +
            "ch.id as ch_id, ch.name as ch_name, ch.photoId as ch_photo from VideoEntity as v " +
            "inner join v.channel as ch " +
            "where v.title = :title " +
            "and v.status = :status " +
            "and v.visible = true " +
            "order by v.publishedDate")
    List<VideoShortInfoMapper> findAllByTitleAndStatusAndVisible(@Param("title") String title, @Param("status") VideoStatus status);

    @Query(value = "select v.id as v_id,v.title as v_title,v.previewPhotoId as v_preview_photo," +
            "v.publishedDate as v_published_date,v.viewCount as v_view_count, v.duration as v_duration," +
            "ch.id as ch_id, ch.name as ch_name, ch.photoId as ch_photo from VideoEntity as v " +
            "inner join v.channel as ch " +
            "where v.visible = true " +
            "order by v.createdDate")
    Page<VideoShortInfoMapper> findAllByIdMapper(Pageable pageable);

    @Query(value = "select v.id as v_id,v.title as v_title,v.previewPhotoId as v_preview_photo," +
            "v.publishedDate as v_published_date,v.viewCount as v_view_count, v.duration as v_duration," +
            "ch.id as ch_id, ch.name as ch_name, ch.photoId as ch_photo from VideoEntity as v " +
            "inner join v.channel as ch " +
            "inner join v.category as c " +
            "where c.id = :categoryId " +
            "and v.status = :status " +
            "and v.visible = true " +
            "order by v.createdDate")
    Page<VideoShortInfoMapper> findAllByCategoryIdAndStatusAndVisible(@Param("categoryId") UUID categoryId, @Param("status") VideoStatus status, Pageable pageable);

    @Query(value = "select v.id as v_id,v.title as v_title,v.previewPhotoId as v_preview_photo," +
            "v.publishedDate as v_published_date,v.viewCount as v_view_count, v.duration as v_duration," +
            "ch.id as ch_id, ch.name as ch_name, ch.photoId as ch_photo from VideoTagEntity as vt " +
            "inner join vt.video as v " +
            "inner join v.channel as ch " +
            "where vt.tagId = :tagId " +
            "and v.status = :status " +
            "and v.visible = true " +
            "order by vt.createdDate")
    Page<VideoShortInfoMapper> findAllByTagIdAndStatusAndVisible(@Param("tagId") UUID tagId, @Param("status") VideoStatus status, Pageable pageable);

    @Query(value = "select v.id as v_id,v.title as v_title,v.previewPhotoId as v_preview_photo," +
            "v.publishedDate as v_published_date,v.viewCount as v_view_count, v.duration as v_duration," +
            "ch.id as ch_id, ch.name as ch_name, ch.photoId as ch_photo from VideoEntity as v " +
            "inner join v.channel as ch " +
            "where ch.id = :channelId " +
            "and v.status = :status " +
            "and v.visible = true " +
            "order by v.createdDate")
    Page<VideoShortInfoMapper> findAllByChannelIdAndStatusAndVisible(@Param("channelId") UUID channelId, @Param("status") VideoStatus status,
                                                                     Pageable pageable);

    @Query(value = "select v.id as v_id,v.title as v_title,v.description as v_description,v.previewPhotoId as v_preview_photo,v.video as v_video," +
            "v.publishedDate as v_published_date,v.viewCount as v_view_count,v.sharedCount as v_shared_count, v.duration as v_duration," +
            "v.type as v_type, v.status as v_status," +
            "c.id as c_id,c.name as c_name," +
            "ch.id as ch_id, ch.name as ch_name, ch.photoId as ch_photo " +
            "from VideoEntity as v " +
            "inner join v.channel as ch " +
            "inner join v.category as c " +
            "where v.id = :id " +
            "and v.visible = true ")
    Optional<VideoFullInfoMapper> findByIdMapper(@Param("id") UUID id);

    Optional<VideoEntity> findByIdAndVisible(UUID id, Boolean visible);

    @Transactional
    @Modifying
    @Query(value = "update VideoEntity set viewCount = viewCount + 1 where id =:id")
    void updateViewCount(@Param("id") UUID id);

    @Transactional
    @Modifying
    @Query(value = "update VideoEntity set sharedCount = sharedCount + 1 where id =:id")
    void updateShareCount(@Param("id") UUID id);

    Optional<VideoEntity> findByIdAndStatusAndVisible(UUID id, VideoStatus status, Boolean visible);

    @Transactional
    @Modifying
    @Query(value = "update VideoEntity set visible = false, videoId = null, previewPhoto = null where id =:id")
    void updateVisible(@Param("id") UUID id);

    @Transactional
    @Modifying
    @Query(value = "update VideoEntity set previewPhotoId = :attachId where id =:id")
    void updatePreviewPhoto(@Param("attachId") UUID attachId, UUID id);
}