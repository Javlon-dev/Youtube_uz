package com.company.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CommentAdminInfoMapper {

    UUID getC_id();
    String getC_content();
    LocalDateTime getC_created_date();

    UUID getV_id();
    String getV_title();
    String getV_description();
    UUID getV_preview_photo();
    Long getV_duration();

}
