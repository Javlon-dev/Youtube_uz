package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class TagDTO extends BaseDTO {

    @NotBlank(message = "Name required")
    private String name;

    private String searchUrl;

    public TagDTO(String name, String searchUrl) {
        this.name = name;
        this.searchUrl = searchUrl;
    }
}
