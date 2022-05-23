package com.company.mapper;

import java.util.UUID;

public interface VideoLikeInfoMapper {

    UUID getVl_id();

    UUID getV_id();
    String getV_title();
    Long getV_duration();
    UUID getV_preview_photo();

    UUID getCh_id();
    String getCh_name();

}
