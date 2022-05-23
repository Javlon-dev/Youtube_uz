package com.company.repository;

import com.company.entity.SubscriptionEntity;
import com.company.enums.NotificationType;
import com.company.enums.SubscriptionStatus;
import com.company.mapper.SubscriptionInfoMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {

    Optional<SubscriptionEntity> findByChannelIdAndProfileId(UUID channelId, UUID profileId);

    @Query(value = "select s.id as s_id, s.notificationType as s_type," +
            "c.id as c_id, c.name as c_name, c.photoId as c_photo " +
            "from SubscriptionEntity s " +
            "inner join s.channel as c " +
            "where s.id = :id")
    Optional<SubscriptionInfoMapper> findByIdMapper(@Param("id") UUID id);

    @Query(value = "select s.id as s_id, s.notificationType as s_type,s.createdDate as s_created_date," +
            "c.id as c_id, c.name as c_name, c.photoId as c_photo " +
            "from SubscriptionEntity s " +
            "inner join s.channel as c " +
            "where s.profileId = :profileId " +
            "and s.status = :status " +
            "order by s.channel.name asc")
    List<SubscriptionInfoMapper> findAllByProfileIdAndStatus(@Param("profileId") UUID profileId,
                                                             @Param("status") SubscriptionStatus status);

    @Transactional
    @Modifying
    @Query("update SubscriptionEntity set status = :status where id = :id")
    void updateStatus(@Param("status") SubscriptionStatus status, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update SubscriptionEntity set notificationType = :type where id = :id")
    void updateNotification(@Param("type") NotificationType type, @Param("id") UUID id);

    @Query(value = "select count(s.id) " +
            "from SubscriptionEntity s " +
            "inner join s.channel c " +
            "where c.id = :channelId")
    Integer channelSubscriptionCount(@Param("channelId") UUID channelId);
}