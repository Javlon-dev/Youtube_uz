package com.company.mapper;

import com.company.enums.VideoStatus;
import com.company.enums.VideoType;

import java.time.LocalDateTime;
import java.util.UUID;

public interface VideoFullInfoMapper {

    UUID getV_id();
    String getV_title();
    String getV_description();
    VideoType getV_type();
    VideoStatus getV_status();
    UUID getV_preview_photo();
    UUID getV_video();
    Long getV_duration();
    Integer getV_view_count();
    Integer getV_shared_count();
    LocalDateTime getV_published_date();

    UUID getC_id();
    String getC_name();

    UUID getCh_id();
    String getCh_name();
    UUID getCh_photo();

}
