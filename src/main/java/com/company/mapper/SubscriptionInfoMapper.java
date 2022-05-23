package com.company.mapper;

import com.company.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SubscriptionInfoMapper {

    UUID getS_id();
    NotificationType getS_type();
    LocalDateTime getS_created_date();

    UUID getC_id();
    String getC_name();
    UUID getC_photo();

}
