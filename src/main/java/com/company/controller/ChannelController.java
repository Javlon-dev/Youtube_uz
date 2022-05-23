package com.company.controller;

import com.company.dto.AttachDTO;
import com.company.dto.ChannelAboutDTO;
import com.company.dto.ChannelDTO;
import com.company.enums.ProfileRole;
import com.company.service.ChannelService;
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
@RequestMapping("/channel")
@RequiredArgsConstructor
@Api(tags = "Channel")
public class ChannelController {

    private final ChannelService channelService;

    /**
     * PUBLIC
     */

    @ApiOperation(value = "Get", notes = "Method used for get channel info")
    @GetMapping("/{channelId}")
    public ResponseEntity<?> get(@PathVariable("channelId") String channelId) {
        log.info("/{channelId} {}", channelId);
        return ResponseEntity.ok(channelService.get(channelId));
    }

    @ApiOperation(value = "Create", notes = "Method used for create channel",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid ChannelDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        return ResponseEntity.ok(channelService.create(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Update About", notes = "Method used for update about of channel",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/{channelId}")
    public ResponseEntity<?> updateAbout(@RequestBody @Valid ChannelAboutDTO dto,
                                         @PathVariable("channelId") String channelId,
                                         HttpServletRequest request) {
        log.info("UPDATE about {}", dto);
        return ResponseEntity.ok(channelService.updateAbout(dto, channelId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Channel Image", notes = "Method used for update channel image",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/image/{channelId}")
    public ResponseEntity<?> channelImage(@RequestBody @Valid AttachDTO dto,
                                          @PathVariable("channelId") String channelId,
                                          HttpServletRequest request) {
        log.info("/public/image/{channelId} {}", dto);
        return ResponseEntity.ok(channelService.channelImage(dto.getId(), channelId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Channel Banner", notes = "Method used for update channel banner",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/banner/{channelId}")
    public ResponseEntity<?> channelBanner(@RequestBody @Valid AttachDTO dto,
                                           @PathVariable("channelId") String channelId,
                                           HttpServletRequest request) {
        log.info("/public/banner/{channelId} {}", dto);
        return ResponseEntity.ok(channelService.channelBanner(dto.getId(), channelId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Profile Channel List", notes = "Method used for get profile's channels list")
    @GetMapping("/public/list")
    public ResponseEntity<?> profileChannelList(HttpServletRequest request) {
        log.info("/public/list");
        return ResponseEntity.ok(channelService.profileChannelList(JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Delete", notes = "Method used for delete channel only owner delete own channels",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/public/{channelId}/delete")
    public ResponseEntity<?> delete(@PathVariable("channelId") String channelId,
                                    HttpServletRequest request) {
        log.info("/public/{channelId}/delete {}", channelId);
        return ResponseEntity.ok(channelService.delete(channelId, JwtUtil.getIdFromHeader(request)));
    }

    /**
     * ADMIN AND USER(OWNER)
     */

    @ApiOperation(value = "Change Status", notes = "Method used for change channel's status with admin or owner",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/status/{channelId}")
    public ResponseEntity<?> changeStatus(@PathVariable("channelId") String channelId,
                                          HttpServletRequest request) {
        log.info("/public/status/{channelId} {}", channelId);
        return ResponseEntity.ok(channelService.changeStatus(channelId, JwtUtil.getIdFromHeader(request)));
    }

    /**
     * ADMIN
     */

    @ApiOperation(value = "List", notes = "Method used for get list of channels",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm/list")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                  HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(channelService.list(page, size));
    }
}
