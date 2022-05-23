package com.company.controller;

import com.company.dto.*;
import com.company.enums.ProfileRole;
import com.company.service.PlaylistService;
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
@RequestMapping("/playlist")
@RequiredArgsConstructor
@Api(tags = "Playlist")
public class PlaylistController {

    private final PlaylistService playlistService;

    /**
     * PUBLIC
     */

    @ApiOperation(value = "Get", notes = "Method used for get playlist info")
    @GetMapping("/{playlistId}")
    public ResponseEntity<?> get(@PathVariable("playlistId") String playlistId) {
        log.info("/{playlistId} {}", playlistId);
        return ResponseEntity.ok(playlistService.get(playlistId));
    }

    @ApiOperation(value = "Create", notes = "Method used for create playlist",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("public/{channelId}")
    public ResponseEntity<?> create(@RequestBody @Valid PlaylistDTO dto,
                                    @PathVariable("channelId") String channelId,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        return ResponseEntity.ok(playlistService.create(dto, channelId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Update About", notes = "Method used for update about of playlist",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/{playlistId}")
    public ResponseEntity<?> updateAbout(@RequestBody @Valid PlaylistAboutDTO dto,
                                         @PathVariable("playlistId") String playlistId,
                                         HttpServletRequest request) {
        log.info("UPDATE about {}", dto);
        return ResponseEntity.ok(playlistService.updateAbout(dto, playlistId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Channel Playlists", notes = "Method used for get channel's playlists")
    @GetMapping("/public/list/{channelId}")
    public ResponseEntity<?> channelPlaylists(@PathVariable("channelId") String channelId) {
        log.info("/public/list/{channelId} {}", channelId);
        return ResponseEntity.ok(playlistService.channelPlaylists(channelId));
    }

    @ApiOperation(value = "Delete", notes = "Method used for delete playlist only owner delete own playlists",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/public/{playlistId}/delete")
    public ResponseEntity<?> delete(@PathVariable("playlistId") String playlistId,
                                    HttpServletRequest request) {
        log.info("/public/{playlistId}/delete {}", playlistId);
        return ResponseEntity.ok(playlistService.delete(playlistId, JwtUtil.getIdFromHeader(request)));
    }

    /**
     * ADMIN AND USER(OWNER)
     */

    @ApiOperation(value = "Change Status", notes = "Method used for change playlist's status with admin or owner",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/status/{playlistId}")
    public ResponseEntity<?> changeStatus(@PathVariable("playlistId") String playlistId,
                                          HttpServletRequest request) {
        log.info("/public/status/{playlistId} {}", playlistId);
        return ResponseEntity.ok(playlistService.changeStatus(playlistId, JwtUtil.getIdFromHeader(request)));
    }

    /**
     * ADMIN
     */

    @ApiOperation(value = "List", notes = "Method used for get list of playlists",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm/list")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                  HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(playlistService.list(page, size));
    }

    @ApiOperation(value = "Profile's Playlist", notes = "Method used for get profile's playlists",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm/{profileId}/list")
    public ResponseEntity<?> profilePlaylist(@PathVariable("profileId") String profileId,
                                             HttpServletRequest request) {
        log.info("/adm/{profileId}/list {}", profileId);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(playlistService.profilePlaylist(profileId));
    }
}
