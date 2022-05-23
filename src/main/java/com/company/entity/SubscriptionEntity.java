package com.company.entity;

import com.company.enums.NotificationType;
import com.company.enums.SubscriptionStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "subscription", uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "channel_id"}))
@Getter
@Setter
public class SubscriptionEntity extends BaseEntity {

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Column(name = "channel_id", nullable = false)
    private UUID channelId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    private ChannelEntity channel;

    @Column
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
}
