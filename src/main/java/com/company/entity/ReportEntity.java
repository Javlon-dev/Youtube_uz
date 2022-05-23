package com.company.entity;

import com.company.enums.ReportType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "report")
@Getter
@Setter
public class ReportEntity extends BaseEntity {

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Column
    private String content;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType type;

}
