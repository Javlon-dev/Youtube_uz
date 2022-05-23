package com.company.entity;

import com.company.enums.CommentLikeType;
import com.company.enums.VideoLikeType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "comment_like")
@Getter
@Setter
public class CommentLikeEntity extends BaseEntity{

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Column(name = "comment_id", nullable = false)
    private UUID commentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private CommentEntity comment;

    @Column
    @Enumerated(EnumType.STRING)
    private CommentLikeType type;
}
