package com.company.controller;

import com.company.dto.*;
import com.company.enums.ProfileRole;
import com.company.service.CommentService;
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
@RequestMapping("/comment")
@RequiredArgsConstructor
@Api(tags = "Comment")
public class CommentController {

    private final CommentService commentService;

    /**
     * PUBLIC
     */

    @ApiOperation(value = "Get", notes = "Method used for get comment info")
    @GetMapping("/{commentId}")
    public ResponseEntity<?> get(@PathVariable("commentId") String commentId) {
        log.info("/{commentId} {}", commentId);
        return ResponseEntity.ok(commentService.get(commentId));
    }

    @ApiOperation(value = "Create", notes = "Method used for create comment",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid CommentDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        return ResponseEntity.ok(commentService.create(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Create Comment Reply", notes = "Method used for create comment",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("/public/reply")
    public ResponseEntity<?> create(@RequestBody @Valid CommentReplyDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE Reply {}", dto);
        return ResponseEntity.ok(commentService.createReplyId(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Update Content", notes = "Method used for update about of channel",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/{commentId}")
    public ResponseEntity<?> updateContent(@RequestBody @Valid CommentContentDTO dto,
                                           @PathVariable("commentId") String commentId,
                                           HttpServletRequest request) {
        log.info("UPDATE Content {}", dto);
        return ResponseEntity.ok(commentService.updateContent(dto, commentId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Profile Comment List", notes = "Method used for get profile's comments")
    @GetMapping("/public/list")
    public ResponseEntity<?> paginationByProfileId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "5") int size,
                                                   HttpServletRequest request) {
        log.info("/public/list");
        return ResponseEntity.ok(commentService.paginationByProfileId(page, size, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Video's Comment List", notes = "Method used for get video's comments")
    @GetMapping("/video/{videoId}")
    public ResponseEntity<?> paginationByVideoId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "5") int size,
                                                 @PathVariable("videoId") String videoId) {
        log.info("/video/{videoId} {}", videoId);
        return ResponseEntity.ok(commentService.paginationByVideoId(page, size, videoId));
    }

    @ApiOperation(value = "Comment's Reply List", notes = "Method used for get comment's reply")
    @GetMapping("/reply/{commentId}")
    public ResponseEntity<?> paginationReplyByCommentId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "5") int size,
                                                 @PathVariable("commentId") String commentId) {
        log.info("/reply/{commentId} {}", commentId);
        return ResponseEntity.ok(commentService.paginationReplyByCommentId(page, size, commentId));
    }

    /**
     * ADMIN AND USER(OWNER)
     */

    @ApiOperation(value = "Delete", notes = "Method used for delete comment only owner delete own comments",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/public/{commentId}/delete")
    public ResponseEntity<?> delete(@PathVariable("commentId") String commentId,
                                    HttpServletRequest request) {
        log.info("/public/{commentId}/delete {}", commentId);
        return ResponseEntity.ok(commentService.delete(commentId, JwtUtil.getIdFromHeader(request)));
    }

    /**
     * ADMIN
     */

    @ApiOperation(value = "List", notes = "Method used for get list of channels",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm/list/{profileId}")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                  @PathVariable("profileId") String profileId,
                                  HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(commentService.paginationByProfileId(page, size, profileId));
    }

    @ApiOperation(value = "List", notes = "Method used for get list of channels",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm/list")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                  HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(commentService.pagination(page, size));
    }
}
