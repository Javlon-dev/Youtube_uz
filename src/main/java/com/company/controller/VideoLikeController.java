package com.company.controller;

import com.company.dto.VideoLikeDTO;
import com.company.enums.ProfileRole;
import com.company.service.VideoLikeService;
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
@RequestMapping("/video-like")
@RequiredArgsConstructor
@Api(tags = "Video Like")
public class VideoLikeController {

    private final VideoLikeService videoLikeService;

    /**
     * PUBLIC
     */

    @ApiOperation(value = "Get", notes = "Method used for get video's like by profile",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/public/{videoId}")
    public ResponseEntity<?> get(@PathVariable("videoId") String videoId,
                                 HttpServletRequest request) {
        log.info("/public/{videoId} {}", videoId);
        return ResponseEntity.ok(videoLikeService.get(videoId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Create", notes = "Method used for create like to video",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid VideoLikeDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        return ResponseEntity.ok(videoLikeService.create(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Delete", notes = "Method used for delete like from video only owner delete own like",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/public/{likeId}/delete")
    public ResponseEntity<?> delete(@PathVariable("likeId") String likeId,
                                    HttpServletRequest request) {
        log.info("/public/{likeId}/delete {}", likeId);
        return ResponseEntity.ok(videoLikeService.delete(likeId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Profile Liked Videos", notes = "Method used for get liked videos by profile")
    @GetMapping("public/liked-video")
    public ResponseEntity<?> getByProfileLikedVideo(@RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "5") int size,
                                                    HttpServletRequest request) {
        log.info("public/liked-video  page={} size={}", page, size);
        return ResponseEntity.ok(videoLikeService.getByProfileLikedVideo(page, size, JwtUtil.getIdFromHeader(request)));
    }


    /**
     * ADMIN
     */

    @ApiOperation(value = "Liked Videos by Profile", notes = "Method used for get liked videos by profile",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm/list/{profileId}")
    public ResponseEntity<?> getByProfileLikedVideo(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "5") int size,
                                        @PathVariable("profileId") String profileId,
                                        HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(videoLikeService.getByProfileLikedVideo(page, size, profileId));
    }
}
