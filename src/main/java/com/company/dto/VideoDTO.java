package com.company.dto;

import com.company.dto.AttachDTO;
import com.company.dto.BaseDTO;
import com.company.dto.CategoryDTO;
import com.company.dto.ChannelDTO;
import com.company.enums.VideoStatus;
import com.company.enums.VideoType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class VideoDTO extends BaseDTO {

    @NotNull(message = "Title not be null")
    private String title;

    @NotNull(message = "Description not be null")
    private String description;

    @NotBlank(message = "CategoryId required")
    private String categoryId;
    private CategoryDTO category;

    @NotBlank(message = "ChannelId required")
    private String channelId;
    private ChannelDTO channel;

    @NotBlank(message = "VideoId required")
    private String videoId;
    private AttachDTO video;

    private AttachDTO previewPhoto;

    private VideoStatus status;

    @NotNull(message = "Type not be null")
    private VideoType type;

    private Integer viewCount;

    private Integer sharedCount;

    private VideoLikeDTO likes;
    private List<VideoLikeDTO> profileLikes;

    private LocalDateTime publishedDate;

    private Long duration;

    private String url;

    public VideoDTO(String url) {
        this.url = url;
    }

    public VideoDTO(String id,String title, AttachDTO previewPhoto, Long duration) {
        super.id = id;
        this.title = title;
        this.previewPhoto = previewPhoto;
        this.duration = duration;
    }
}
