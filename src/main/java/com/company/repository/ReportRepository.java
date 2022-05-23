package com.company.repository;

import com.company.entity.ReportEntity;
import com.company.mapper.ReportInfoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {

    @Query("select r.id as r_id,r.entityId as r_entity_id, r.type as r_type," +
            "p.id as p_id, p.name as p_name, p.surname as p_surname, p.attachId as p_photo " +
            "from ReportEntity r " +
            "inner join r.profile p " +
            "where r.id = :id ")
    Optional<ReportInfoMapper> findByIdMapper(@Param("id") UUID id);

    @Query("select r.id as r_id,r.entityId as r_entity_id, r.type as r_type," +
            "p.id as p_id, p.name as p_name, p.surname as p_surname, p.attachId as p_photo " +
            "from ReportEntity r " +
            "inner join r.profile p " +
            "order by r.createdDate desc")
    Page<ReportInfoMapper> findAllMapper(Pageable pageable);

    @Query("select r.id as r_id,r.entityId as r_entity_id, r.type as r_type," +
            "p.id as p_id, p.name as p_name, p.surname as p_surname, p.attachId as p_photo " +
            "from ReportEntity r " +
            "inner join r.profile p " +
            "where r.profileId = :profileId " +
            "order by r.createdDate desc")
    Page<ReportInfoMapper> findAllByProfileIdMapper(@Param("profileId") UUID profileId, Pageable pageable);
}