package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaylistAboutDTO {

    @NotBlank(message = "Name required")
    private String name;

    @NotNull(message = "Description not be null")
    private String description;

    @NotNull(message = "OrderNum not be null")
    @Positive(message = "Invalid Order Num")
    private Integer orderNum;
}
