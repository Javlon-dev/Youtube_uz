package com.company.repository;

import com.company.entity.CommentEntity;
import com.company.mapper.CommentAdminInfoMapper;
import com.company.mapper.CommentInfoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    @Query("select c.id as c_id, c.content as c_content, c.createdDate as c_created_date," +
            "p.id as p_id, p.name as p_name, p.surname as p_surname, p.attachId as p_photo " +
            "from CommentLikeEntity as cl " +
            "inner join cl.comment as c " +
            "inner join cl.profile as p " +
            "where p.visible = true " +
            "order by c.createdDate desc")
    Page<CommentInfoMapper> findAllComments(Pageable pageable);

    @Query("select c.id as c_id, c.content as c_content, c.createdDate as c_created_date," +
            "p.id as p_id, p.name as p_name, p.surname as p_surname, p.attachId as p_photo " +
            "from CommentEntity as c " +
            "inner join c.profile as p " +
            "where c.id = :id " +
            "and p.visible = true " +
            "order by c.createdDate desc")
    Optional<CommentInfoMapper> findByIdMapper(@Param("id") UUID id);

    @Query("select c.id as c_id, c.content as c_content, c.createdDate as c_created_date," +
            "v.id as v_id, v.title as v_title,v.description as v_description, v.previewPhotoId as v_preview_photo, v.duration as v_duration " +
            "from CommentEntity as c " +
            "inner join c.profile as p " +
            "inner join c.video as v " +
            "where p.id = :profileId " +
            "and p.visible = true " +
            "order by c.createdDate desc")
    Page<CommentAdminInfoMapper> findAllByProfileId(@Param("profileId") UUID profileId, Pageable pageable);

    @Query("select c.id as c_id, c.content as c_content, c.createdDate as c_created_date," +
            "p.id as p_id, p.name as p_name, p.surname as p_surname, p.attachId as p_photo " +
            "from CommentEntity as c " +
            "inner join c.profile as p " +
            "inner join c.video as v " +
            "where v.id = :videoId " +
            "and p.visible = true " +
            "order by c.createdDate desc")
    Page<CommentInfoMapper> findAllByVideoId(@Param("videoId") UUID videoId, Pageable pageable);

    @Query("select c.id as c_id, c.content as c_content, c.createdDate as c_created_date," +
            "p.id as p_id, p.name as p_name, p.surname as p_surname, p.attachId as p_photo " +
            "from CommentEntity as c " +
            "inner join c.profile as p " +
            "where c.replyId = :replyId " +
            "and p.visible = true " +
            "order by c.createdDate desc")
    Page<CommentInfoMapper> findAllByReplyId(@Param("replyId") UUID replyId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update CommentEntity set content = :content, updatedDate = :updateDate where id = :id")
    void updateContent(@Param("content") String content, @Param("updateDate") LocalDateTime updatedDate, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update CommentEntity set replyId = :replyId where id = :id")
    void updateReplyId(@Param("replyId") UUID replyId, @Param("id") UUID id);
}