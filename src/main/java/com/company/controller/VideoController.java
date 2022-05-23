package com.company.controller;

import com.company.dto.VideoAboutDTO;
import com.company.dto.VideoDTO;
import com.company.dto.VideoPreviewPhotoDTO;
import com.company.enums.ProfileRole;
import com.company.service.VideoService;
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
@RequestMapping("/video")
@RequiredArgsConstructor
@Api(tags = "Video")
public class VideoController {

    private final VideoService videoService;

    /**
     * PUBLIC
     */

    @ApiOperation(value = "Get", notes = "Method used for get video",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/public/{videoId}")
    public ResponseEntity<?> get(@PathVariable("videoId") String videoId,
                                 HttpServletRequest request) {
        log.info("/public/{videoId} {}", videoId);
        return ResponseEntity.ok(videoService.get(videoId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Create", notes = "Method used for create video",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid VideoDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        return ResponseEntity.ok(videoService.create(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Update About", notes = "Method used for update about of video",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/{videoId}")
    public ResponseEntity<?> updateAbout(@RequestBody @Valid VideoAboutDTO dto,
                                         @PathVariable("videoId") String videoId,
                                         HttpServletRequest request) {
        log.info("UPDATE about {}", dto);
        return ResponseEntity.ok(videoService.updateAbout(dto, videoId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Searching by title", notes = "Method used for get search videos")
    @GetMapping("/search")
    public ResponseEntity<?> searchResult(@RequestParam(value = "search") String search) {
        log.info("/search {}", search);
        return ResponseEntity.ok(videoService.searchResult(search));
    }

    @ApiOperation(value = "Increase View Count", notes = "Method used for increase view count")
    @PutMapping("/view/{videoId}")
    public ResponseEntity<?> increaseViewCount(@PathVariable("videoId") String videoId) {
        log.info("/view/{videoId} {}", videoId);
        videoService.updateViewCount(videoId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Increase Shared Count", notes = "Method used for increase share count")
    @PutMapping("/share/{videoId}")
    public ResponseEntity<?> increaseSharedCount(@PathVariable("videoId") String videoId) {
        log.info("/share/{videoId} {}", videoId);
        return ResponseEntity.ok(videoService.updateShareCount(videoId));
    }

    @ApiOperation(value = "Delete", notes = "Method used for delete video only owner delete own videos",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/public/{videoId}/delete")
    public ResponseEntity<?> delete(@PathVariable("videoId") String videoId,
                                    HttpServletRequest request) {
        log.info("/public/{videoId}/delete {}", videoId);
        return ResponseEntity.ok(videoService.delete(videoId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "List By Category", notes = "Method used for get list of videos by category id")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> paginationByCategoryId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "5") int size,
                                                    @PathVariable("categoryId") String categoryId) {
        log.info("/category/{categoryId} category={} page={} size={}", categoryId, page, size);
        return ResponseEntity.ok(videoService.paginationByCategoryId(page, size, categoryId));
    }

    @ApiOperation(value = "List By Channel", notes = "Method used for get list of videos by channel id")
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<?> paginationByChannelId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "5") int size,
                                                   @PathVariable("channelId") String channelId) {
        log.info("/channel/{channelId} channel={} page={} size={}", channelId, page, size);
        return ResponseEntity.ok(videoService.paginationByChannelId(page, size, channelId));
    }

    @ApiOperation(value = "List By Tag", notes = "Method used for get list of videos by tag id")
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<?> paginationByTagId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "15") int size,
                                                   @PathVariable("tagId") String tagId) {
        log.info("/tag/{tagId} tagId={} page={} size={}", tagId, page, size);
        return ResponseEntity.ok(videoService.paginationByTagId(page, size, tagId));
    }

    @ApiOperation(value = "Preview Photo", notes = "Method used for update preview photo to video",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/preview-photo/{videoId}")
    public ResponseEntity<?> updatePreviewPhoto(@RequestBody @Valid VideoPreviewPhotoDTO dto,
                                          @PathVariable("videoId") String videoId,
                                          HttpServletRequest request) {
        log.info("/public/preview-photo/{videoId} {}", dto);
        return ResponseEntity.ok(videoService.updatePreviewPhoto(dto, videoId, JwtUtil.getIdFromHeader(request)));
    }

    /**
     * ADMIN AND USER(OWNER)
     */

    @ApiOperation(value = "Change Status", notes = "Method used for change video's status with admin or owner",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/status/{videoId}")
    public ResponseEntity<?> changeStatus(@PathVariable("videoId") String videoId,
                                          HttpServletRequest request) {
        log.info("/public/status/{channelId} {}", videoId);
        return ResponseEntity.ok(videoService.changeStatus(videoId, JwtUtil.getIdFromHeader(request)));
    }

    /**
     * ADMIN
     */

    @ApiOperation(value = "List", notes = "Method used for get list of videos",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm/list")
    public ResponseEntity<?> pagination(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "5") int size,
                                        HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(videoService.pagination(page, size));
    }

}
