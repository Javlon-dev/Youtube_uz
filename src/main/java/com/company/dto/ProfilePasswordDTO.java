package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfilePasswordDTO {

    @NotBlank(message = "Old Password required")
    private String oldPassword;

    @NotBlank(message = "Password required")
    @Size(min = 8, message = "Password length must be between 8 or greater than")
    private String newPassword;

}
