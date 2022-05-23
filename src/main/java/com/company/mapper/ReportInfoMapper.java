package com.company.mapper;

import com.company.enums.ReportType;

import java.util.UUID;

public interface ReportInfoMapper {

    UUID getR_id();
    String getR_content();
    UUID getR_entity_id();
    ReportType getR_type();

    UUID getP_id();
    String getP_name();
    String getP_surname();
    UUID getP_photo();

}
