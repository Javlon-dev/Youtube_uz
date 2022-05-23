package com.company.repository;

import com.company.entity.PlaylistEntity;
import com.company.enums.PlaylistStatus;
import com.company.mapper.PlaylistInfoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<PlaylistEntity, UUID> {

    @Transactional
    @Modifying
    @Query("update PlaylistEntity set status = :status where id = :id")
    void updateStatus(@Param("status") PlaylistStatus status, @Param("id") UUID id);

    @Query("select pl.id as pl_id,pl.name as pl_name,pl.description as pl_description,pl.status as pl_status,pl.orderNum as pl_order_num," +
            "pl.createdDate as pl_created_date, " +
            "ch.id as ch_id, ch.name as ch_name, ch.photoId as ch_photo_id," +
            "pr.id as pr_id, pr.name as pr_name,pr.surname as pr_surname,pr.attachId as pr_photo_id " +
            "from PlaylistEntity as pl " +
            "inner join pl.channel as ch " +
            "inner join ch.profile as pr " +
            "where pl.id = :playlistId " +
            "and pl.visible = true " +
            "and ch.visible = true " +
            "order by pl.orderNum desc")
    PlaylistInfoMapper findAllByPlaylistId(@Param("playlistId") UUID playlistId);

    @Query("select pl.id as pl_id,pl.name as pl_name,pl.description as pl_description,pl.status as pl_status,pl.orderNum as pl_order_num," +
            "pl.createdDate as pl_created_date, " +
            "ch.id as ch_id, ch.name as ch_name, ch.photoId as ch_photo_id," +
            "pr.id as pr_id, pr.name as pr_name,pr.surname as pr_surname,pr.attachId as pr_photo_id " +
            "from PlaylistEntity as pl " +
            "inner join pl.channel as ch " +
            "inner join ch.profile as pr " +
            "where ch.id = :channelId " +
            "and pl.status = :status " +
            "and pl.visible = true " +
            "and ch.visible = true " +
            "order by pl.orderNum desc")
    List<PlaylistInfoMapper> findAllByChannelIdAndStatus(@Param("channelId") UUID channelId, @Param("status") PlaylistStatus status);

    @Query("select pl.id as pl_id,pl.name as pl_name,pl.description as pl_description,pl.status as pl_status,pl.orderNum as pl_order_num," +
            "pl.createdDate as pl_created_date," +
            "ch.id as ch_id, ch.name as ch_name, ch.photoId as ch_photo_id," +
            "pr.id as pr_id, pr.name as pr_name,pr.surname as pr_surname,pr.attachId as pr_photo_id " +
            "from PlaylistEntity as pl " +
            "inner join pl.channel as ch " +
            "inner join ch.profile as pr " +
            "where ch.profileId = :profileId " +
            "and pl.visible = true " +
            "and ch.visible = true " +
            "order by pl.orderNum desc")
    List<PlaylistInfoMapper> findAllByProfileId(@Param("profileId") UUID profileId);

    @Query("select pl.id as pl_id,pl.name as pl_name,pl.description as pl_description,pl.status as pl_status,pl.orderNum as pl_order_num," +
            "pl.createdDate as pl_created_date," +
            "ch.id as ch_id, ch.name as ch_name, ch.photoId as ch_photo_id," +
            "pr.id as pr_id, pr.name as pr_name,pr.surname as pr_surname,pr.attachId as pr_photo_id " +
            "from PlaylistEntity as pl " +
            "inner join pl.channel as ch " +
            "inner join ch.profile as pr " +
            "where pl.visible = true " +
            "and ch.visible = true " +
            "order by pl.createdDate desc")
    Page<PlaylistInfoMapper> getPlaylistInfo(Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "update PlaylistEntity set visible = false where id =:id")
    void updateVisible(@Param("id") UUID id);
}