package com.company.entity;

import com.company.enums.ChannelStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "channel")
@Getter
@Setter
public class ChannelEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "photo_id")
    private UUID photoId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    private AttachEntity photo;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelStatus status;

    @Column
    private Boolean visible = true;

    @Column(name = "banner_id")
    private UUID bannerId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_id", insertable = false, updatable = false)
    private AttachEntity banner;

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;


}
