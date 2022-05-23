package com.company.dto;

import com.company.enums.CommentLikeType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class CommentLikeDTO extends BaseDTO{

    private String profileId;
    private ProfileDTO profile;

    @NotBlank(message = "CommentId required")
    private String commentId;
    private CommentDTO comment;

    @NotNull(message = "Type not be null")
    private CommentLikeType type;


}
