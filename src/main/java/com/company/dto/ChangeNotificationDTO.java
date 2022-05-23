package com.company.dto;


import com.company.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangeNotificationDTO {

    @NotBlank(message = "ChannelId required")
    private String channelId;

    @NotNull(message = "NotificationType required")
    private NotificationType notificationType;

}
