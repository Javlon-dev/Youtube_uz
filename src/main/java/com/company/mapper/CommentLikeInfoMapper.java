package com.company.mapper;

import com.company.enums.CommentLikeType;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CommentLikeInfoMapper {

    UUID getCl_id();
    LocalDateTime getCl_created_date();
    CommentLikeType getCl_type();

    UUID getP_id();

    UUID getC_id();

}
