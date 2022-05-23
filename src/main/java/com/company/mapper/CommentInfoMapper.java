package com.company.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CommentInfoMapper {

    UUID getC_id();
    String getC_content();
    LocalDateTime getC_created_date();

    UUID getP_id();
    String getP_name();
    String getP_surname();
    UUID getP_photo();
}
