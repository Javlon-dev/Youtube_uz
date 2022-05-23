package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDTO extends BaseDTO {

    private String profileId;
    private ProfileDTO profile;

    @NotBlank(message = "VideoId required")
    private String videoId;
    private VideoDTO video;

    @NotBlank(message = "Content required")
    private String content;

    private String replyId;

    private Integer likeCount;
    private Integer dislikeCount;
}
