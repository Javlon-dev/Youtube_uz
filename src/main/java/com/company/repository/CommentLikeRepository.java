package com.company.repository;

import com.company.entity.CommentLikeEntity;
import com.company.mapper.CommentLikeInfoMapper;
import com.company.mapper.LikeCountSimpleMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, UUID> {
    Optional<CommentLikeEntity> findByCommentIdAndProfileId(UUID commentId, UUID profileId);

    @Query(value = "select sum(case when type = 'LIKE' THEN 1 else 0 END) like_count," +
            "sum(case when type = 'LIKE' THEN 0 else 1 END) dislike_count " +
            "from comment_like " +
            "where comment_id = :commentId", nativeQuery = true)
    LikeCountSimpleMapper getLikeCountByCommentId(@Param("commentId") UUID commentId);


    @Query(value = "select cl.id as cl_id,cl.type as cl_type, cl.createdDate as cl_created_date," +
            "p.id as p_id, c.id as c_id " +
            "from CommentLikeEntity cl " +
            "inner join cl.profile p " +
            "inner join cl.comment c " +
            "where cl.id = :id " +
            "and p.visible = true ")
    Optional<CommentLikeInfoMapper> findByIdMapper(@Param("id") UUID id);

    @Query(value = "select cl.id as cl_id,cl.type as cl_type, cl.createdDate as cl_created_date," +
            "p.id as p_id, c.id as c_id " +
            "from CommentLikeEntity cl " +
            "inner join cl.profile p " +
            "inner join cl.comment c " +
            "where cl.profileId = :profileId " +
            "and p.visible = true " +
            "order by cl.createdDate desc ")
    Page<CommentLikeInfoMapper> findAllByProfileIdMapper(@Param("profileId") UUID profileId, Pageable pageable);
}