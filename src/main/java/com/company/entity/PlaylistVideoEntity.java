package com.company.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "playlist_video", uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "playlist_id"}))
@Getter
@Setter
public class PlaylistVideoEntity extends BaseEntity {

    @Column(name = "video_id", nullable = false)
    private UUID videoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", insertable = false, updatable = false)
    private VideoEntity video;

    @Column(name = "playlist_id", nullable = false)
    private UUID playlistId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", insertable = false, updatable = false)
    private PlaylistEntity playlist;

    @Column(name = "order_num", nullable = false)
    private Integer orderNum;
}
