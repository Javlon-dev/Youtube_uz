package com.company.dto;

import com.company.enums.EmailType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailDTO extends BaseDTO{

    private String toEmail;

    private EmailType type;
}
