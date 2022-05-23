package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoAboutDTO {

    @NotNull(message = "Title not be null")
    private String title;

    @NotNull(message = "Description not be null")
    private String description;
}
