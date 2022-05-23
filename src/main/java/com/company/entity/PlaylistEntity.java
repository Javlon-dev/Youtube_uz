package com.company.entity;

import com.company.enums.PlaylistStatus;
import com.company.enums.ProfileStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "playlist")
@Getter
@Setter
public class PlaylistEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column
    private Boolean visible = true;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaylistStatus status;

    @Column(name = "order_num", nullable = false)
    private Integer orderNum;

    @Column(name = "channel_id", nullable = false)
    private UUID channelId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    private ChannelEntity channel;

}
