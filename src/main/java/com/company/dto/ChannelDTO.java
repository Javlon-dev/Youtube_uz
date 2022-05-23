package com.company.dto;

import com.company.enums.ChannelStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ChannelDTO extends BaseDTO {

    @NotBlank(message = "Name required")
    private String name;

    @NotNull(message = "Description not be null")
    private String description;

    private String photoId;
    private AttachDTO photo;

    private ChannelStatus status;

    private String bannerId;
    private AttachDTO banner;

    private String profileId;
    private ProfileDTO profile;

    private Integer subscribers;

    private String url;

    public ChannelDTO(String url) {
        this.url = url;
    }

    public ChannelDTO(String id, String name, AttachDTO photo) {
        super.id = id;
        this.name = name;
        this.photo = photo;
    }
}
