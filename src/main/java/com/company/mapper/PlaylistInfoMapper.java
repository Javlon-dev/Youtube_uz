package com.company.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PlaylistInfoMapper {

    UUID getPl_id();
    String getPl_name();
    String getPl_description();
    String getPl_status();
    Integer getOrder_num();
    LocalDateTime getPl_created_date();

    UUID getCh_id();
    String getCh_name();
    UUID getCh_photo_id();

    UUID getPr_id();
    String getPr_name();
    String getPr_surname();
    UUID getPr_photo_id();

}
