package com.company.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.company.annotation.ValidEmail;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDTO {

    @ValidEmail
    private String email;

    @NotBlank(message = "Password required")
    @Size(min = 8, message = "Password length must be between 8 or greater than")
    private String password;

}
