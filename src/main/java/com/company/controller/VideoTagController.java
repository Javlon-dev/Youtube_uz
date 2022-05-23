package com.company.controller;

import com.company.dto.VideoLikeDTO;
import com.company.dto.VideoTagDTO;
import com.company.service.VideoTagService;
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
@RequestMapping("/video-tag")
@RequiredArgsConstructor
@Api(tags = "Video Tag")
public class VideoTagController {

    private final VideoTagService videoTagService;

    /**
     * PUBLIC
     */

    @ApiOperation(value = "Add", notes = "Method used for add tags to video",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("/public")
    public ResponseEntity<?> add(@RequestBody @Valid VideoTagDTO dto,
                                    HttpServletRequest request) {
        log.info("/public {}", dto);
        return ResponseEntity.ok(videoTagService.add(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Delete", notes = "Method used for delete tags from video only owner delete own tags",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/public/delete")
    public ResponseEntity<?> delete(@RequestBody @Valid VideoTagDTO dto,
                                    HttpServletRequest request) {
        log.info("/public/delete {}", dto);
        return ResponseEntity.ok(videoTagService.delete(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Video Tags", notes = "Method used for get tags from videos")
    @GetMapping("/{videoId}")
    public ResponseEntity<?> getAllByVideoId(@PathVariable("videoId") String videoId) {
        log.info("/{videoId}  {}", videoId);
        return ResponseEntity.ok(videoTagService.getAllByVideoId(videoId));
    }
}
