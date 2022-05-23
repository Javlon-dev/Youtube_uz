package com.company.controller;

import com.company.dto.ChangeNotificationDTO;
import com.company.dto.SubscriptionDTO;
import com.company.enums.ProfileRole;
import com.company.service.SubscriptionService;
import com.company.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
@Api(tags = "Subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * PUBLIC
     */

    @ApiOperation(value = "Create", notes = "Method used for subscription")
    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid SubscriptionDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        return ResponseEntity.ok(subscriptionService.create(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Status", notes = "Method used for change subscription status")
    @PutMapping("/public/{channelId}")
    public ResponseEntity<?> changeStatus(@PathVariable("channelId") String channelId,
                                          HttpServletRequest request) {
        log.info("/public/{channelId} {}", channelId);
        return ResponseEntity.ok(subscriptionService.changeStatus(channelId, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Notification", notes = "Method used for change notification type")
    @PutMapping("/public/notification")
    public ResponseEntity<?> changeNotification(@RequestBody @Valid ChangeNotificationDTO dto,
                                                HttpServletRequest request) {
        log.info("/public/{channelId} {}", dto);
        return ResponseEntity.ok(subscriptionService.changeNotification(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Subscriptions List", notes = "Method used for subscription list by profile")
    @GetMapping("/public")
    public ResponseEntity<?> subscriptionListByProfileId(HttpServletRequest request) {
        log.info("/public");
        return ResponseEntity.ok(subscriptionService.subscriptionListByProfileId(JwtUtil.getIdFromHeader(request)));
    }


    /**
     * ADMIN
     */

    @ApiOperation(value = "Subscriptions List", notes = "Method used for subscription list by profile")
    @GetMapping("/adm/{profileId}")
    public ResponseEntity<?> subscriptionListByProfileId(@PathVariable("profileId") String profileId,
                                                         HttpServletRequest request) {
        log.info("/adm/{profileId} {}", profileId);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(subscriptionService.subscriptionListByProfileId(profileId));
    }
}
