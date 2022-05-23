package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachDTO extends BaseDTO {

    private String path;

    private String extension;

    private String originalName;

    private Long fileSize;

    private String url;

    public AttachDTO(String url) {
        this.url = url;
    }
}
