package com.company.controller;

import com.company.dto.*;
import com.company.enums.ProfileRole;
import com.company.service.PlaylistVideoService;
import com.company.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/playlist-video")
@RequiredArgsConstructor
@Api(tags = "Playlist Video")
public class PlaylistVideoController {

    private final PlaylistVideoService playlistVideoService;

    /**
     * PUBLIC
     */

    @ApiOperation(value = "Get", notes = "Method used for get playlist video info")
    @GetMapping("/{playlistVideoId}")
    public ResponseEntity<?> get(@PathVariable("playlistVideoId") String playlistVideoId) {
        log.info("/{playlistVideoId} {}", playlistVideoId);
        return ResponseEntity.ok(playlistVideoService.get(playlistVideoId));
    }

    @ApiOperation(value = "Create", notes = "Method used for add video to playlist",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid PlaylistVideoDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        return ResponseEntity.ok(playlistVideoService.create(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Update", notes = "Method used for update update order num videos in the playlist",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/{playlistVideoId}")
    public ResponseEntity<?> update(@RequestBody @Valid UpdateOrderNumDTO dto,
                                    @PathVariable("playlistVideoId") String playlistVideoId,
                                    HttpServletRequest request) {
        log.info("/public/{playlistVideoId} {}", dto);
        return ResponseEntity.ok(playlistVideoService.update(dto, playlistVideoId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Delete", notes = "Method used for delete playlist video only owner deleted",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/public/delete")
    public ResponseEntity<?> delete(@RequestBody @Valid PlaylistVideoIdDTO dto,
                                    HttpServletRequest request) {
        log.info("/public/delete {}", dto);
        return ResponseEntity.ok(playlistVideoService.delete(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Videos By Playlist", notes = "Method used for get videos by playlist")
    @GetMapping("/list/{playlistId}")
    public ResponseEntity<?> videosByPlaylistId(@PathVariable("playlistId") String playlistId) {
        log.info("LIST {}", playlistId);
        return ResponseEntity.ok(playlistVideoService.videosByPlaylistId(playlistId));
    }

}
