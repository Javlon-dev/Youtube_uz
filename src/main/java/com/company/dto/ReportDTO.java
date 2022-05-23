package com.company.dto;

import com.company.enums.ReportType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportDTO extends BaseDTO {

    private String profileId;
    private ProfileDTO profileDTO;

    @NotBlank(message = "Content required")
    private String content;

    @NotBlank(message = "EntityId required")
    private String entityId;

    @NotNull(message = "Type required")
    private ReportType type;

}
