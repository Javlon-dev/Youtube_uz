package com.company.dto;

import com.company.enums.NotificationType;
import com.company.enums.SubscriptionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionDTO extends BaseDTO {

    private String profileId;
    private ProfileDTO profile;

    @NotBlank(message = "ChannelId required")
    private String channelId;
    private ChannelDTO channel;

    private SubscriptionStatus status;

    private NotificationType notificationType;

}

