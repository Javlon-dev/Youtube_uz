package com.company.mapper;

import java.time.LocalDateTime;

public interface PlaylistVideoInfoMapper {

    String getPv_id();
    LocalDateTime getPv_created_date();
    Integer getPv_order_num();

    String getPl_id();

    String getV_id();
    String getV_preview_photo();
    String getV_title();
    Long getV_duration();

    String getCh_id();
    String getCh_name();
    String getCh_photo();
}
