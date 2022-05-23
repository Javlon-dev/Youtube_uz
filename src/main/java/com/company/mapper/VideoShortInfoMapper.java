package com.company.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

public interface VideoShortInfoMapper {

    UUID getV_id();
    String getV_title();
    UUID getV_preview_photo();
    LocalDateTime getV_published_date();
    Integer getV_view_count();
    Long getV_duration();

    UUID getCh_id();
    String getCh_name();
    UUID getCh_photo();

}
