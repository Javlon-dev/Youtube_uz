package com.company.repository;

import com.company.entity.ChannelEntity;
import com.company.enums.ChannelStatus;
import com.company.enums.ProfileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<ChannelEntity, UUID> {

    @Transactional
    @Modifying
    @Query("update ChannelEntity set photoId = :attachId where id = :id")
    int updatePhoto(@Param("attachId") UUID attachId, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update ChannelEntity set bannerId = :attachId where id = :id")
    int updateBanner(@Param("attachId") UUID attachId, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update ChannelEntity set name = :name , description = :description , updatedDate = :now where id = :id")
    int updateBio(@Param("name") String name, @Param("description") String description,
                  @Param("now") LocalDateTime now, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update ChannelEntity set status = :status where id = :id")
    int updateStatus(@Param("status") ChannelStatus status, @Param("id") UUID id);

    List<ChannelEntity> findAllByProfileId(UUID profileId, Sort sort);

    Page<ChannelEntity> findAllByStatus(ChannelStatus status, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update ChannelEntity set visible = false , photoId = null , bannerId = null where id = :id")
    void updateVisible(@Param("id") UUID id);


    Optional<ChannelEntity> findByIdAndVisible(UUID id, Boolean visible);
}