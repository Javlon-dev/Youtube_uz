package com.company.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attach")
@Getter
@Setter
public class AttachEntity extends BaseEntity {

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String extension;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column
    private Long duration;
}