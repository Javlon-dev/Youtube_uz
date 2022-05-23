package com.company.entity;


import com.company.enums.VideoLikeType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "video_like")
@Getter
@Setter
public class VideoLikeEntity extends BaseEntity {

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Column(name = "video_id", nullable = false)
    private UUID videoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", insertable = false, updatable = false)
    private VideoEntity video;

    @Column
    @Enumerated(EnumType.STRING)
    private VideoLikeType type;

}
