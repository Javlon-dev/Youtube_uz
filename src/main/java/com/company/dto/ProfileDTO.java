package com.company.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.company.annotation.ValidEmail;
import com.company.enums.ProfileRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ProfileDTO extends BaseDTO {

    @NotBlank(message = "Name required")
    private String name;

    @NotBlank(message = "Surname required")
    private String surname;

    @ValidEmail
    private String email;

    @NotBlank(message = "Password required")
    @Size(min = 8, message = "Password length must be between 8 or greater than")
    private String password;

    private String attachId;

    private String status;

    @NotNull(message = "Role not be null")
    private ProfileRole role;

    private String jwt;

    private AttachDTO image;


    private String url;

    public ProfileDTO(String url) {
        this.url = url;
    }
}
