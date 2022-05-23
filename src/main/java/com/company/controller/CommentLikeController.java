package com.company.controller;

import com.company.dto.CommentLikeDTO;
import com.company.dto.VideoLikeDTO;
import com.company.enums.ProfileRole;
import com.company.service.CommentLikeService;
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
@RequestMapping("/comment-like")
@RequiredArgsConstructor
@Api(tags = "Comment Like")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;


    /**
     * PUBLIC
     */

    @ApiOperation(value = "Create", notes = "Method used for create like to comment",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid CommentLikeDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        return ResponseEntity.ok(commentLikeService.create(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Delete", notes = "Method used for delete like from comment only owner delete own like",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/public/{commentLikeId}/delete")
    public ResponseEntity<?> delete(@PathVariable("commentLikeId") String commentLikeId,
                                    HttpServletRequest request) {
        log.info("/public/{commentLikeId}/delete {}", commentLikeId);
        return ResponseEntity.ok(commentLikeService.delete(commentLikeId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Profile Liked Comments", notes = "Method used for get liked comments by profile")
    @GetMapping("/public/liked-comment")
    public ResponseEntity<?> getByProfileLikedComment(@RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "5") int size,
                                                    HttpServletRequest request) {
        log.info("public/liked-video  page={} size={}", page, size);
        return ResponseEntity.ok(commentLikeService.getByProfileLikedComment(page, size, JwtUtil.getIdFromHeader(request)));
    }


    /**
     * ADMIN
     */

    @ApiOperation(value = "Liked Comments by Profile", notes = "Method used for get liked comments by profile",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm/list/{profileId}")
    public ResponseEntity<?> getByProfileLikedComment(@RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "5") int size,
                                                    @PathVariable("profileId") String profileId,
                                                    HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(commentLikeService.getByProfileLikedComment(page, size, profileId));
    }
}
