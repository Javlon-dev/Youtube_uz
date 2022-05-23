package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoTagDTO extends BaseDTO {

    @NotBlank(message = "VideoId required")
    private String videoId;
    private VideoDTO video;

    @NotBlank(message = "TagId required")
    private String tagId;
    private TagDTO tag;

}
