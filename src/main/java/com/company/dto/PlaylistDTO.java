package com.company.dto;

import com.company.enums.PlaylistStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaylistDTO extends BaseDTO {

    @NotBlank(message = "Name required")
    private String name;

    @NotNull(message = "Description not be null")
    private String description;

    private PlaylistStatus status;

    @NotNull(message = "OrderNum not be null")
    @Positive(message = "Invalid OrderNum")
    private Integer orderNum;

    private String channelId;
    private ChannelDTO channel;

    private Integer videoCount;
    private List<VideoDTO> videoList;
}
