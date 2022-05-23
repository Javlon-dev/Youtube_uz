package com.company.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class VideoPlaylistDTO {

    private List<VideoDTO> video;

    private List<PlaylistDTO> playlist;

    public VideoPlaylistDTO(List<VideoDTO> video, List<PlaylistDTO> playlist) {
        this.video = video;
        this.playlist = playlist;
    }
}
