package com.company.controller;

import com.company.dto.*;
import com.company.enums.ProfileRole;
import com.company.service.ProfileService;
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
@RequestMapping("/profile")
@RequiredArgsConstructor
@Api(tags = "Profile")
public class ProfileController {

    private final ProfileService profileService;


    /**
     * PUBLIC
     */

    @ApiOperation(value = "Get", notes = "Method used for get profile info")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        log.info("/{id} {}", id);
        return ResponseEntity.ok(profileService.get(id));
    }

    @ApiOperation(value = "Update Bio", notes = "Method used for update profile's bio",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public")
    public ResponseEntity<?> updateBio(@RequestBody @Valid ProfileBioDTO dto,
                                       HttpServletRequest request) {
        log.info("Update Bio {}", dto);
        String id = JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(profileService.updateBio(id, dto));
    }

    @ApiOperation(value = "Profile Image", notes = "Method used for update profile's image",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/image")
    public ResponseEntity<?> profileImage(@RequestBody @Valid AttachDTO dto,
                                          HttpServletRequest request) {
        log.info("/public/image {}", dto);
        return ResponseEntity.ok(profileService.profileImage(dto.getId(), JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Email Reset", notes = "Method used for reset profile's email",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/email")
    public ResponseEntity<?> emailReset(@RequestBody @Valid ProfileEmailDTO dto,
                                        HttpServletRequest request) {
        log.info("/public/email {}", dto);
        return ResponseEntity.ok(profileService.emailReset(dto, JwtUtil.getIdFromHeader(request)));
    }

    @ApiOperation(value = "Email Verification", notes = "Method used for email verifier")
    @GetMapping("/email/{jwt}")
    public ResponseEntity<?> emailConfirm(@PathVariable("jwt") String jwt) {
        log.info("/email/{jwt} {}", jwt);
        ProfileJwtDTO dto = JwtUtil.decode(jwt);
        return ResponseEntity.ok(profileService.emailConfirm(dto.getId(), dto.getEmail()));
    }

    @ApiOperation(value = "Change Password", notes = "Method used for change profile's password",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/public/change")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ProfilePasswordDTO dto,
                                            HttpServletRequest request) {
        log.info("/public/change {}", dto);
        return ResponseEntity.ok(profileService.changePassword(dto, JwtUtil.getIdFromHeader(request)));
    }

    /**
     * ADMIN
     */

    @ApiOperation(value = "Create", notes = "Method used for create profile",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("/adm")
    public ResponseEntity<?> create(@RequestBody @Valid ProfileDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(profileService.create(dto));
    }

    @ApiOperation(value = "List", notes = "Method used for get list of profiles",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                  HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(profileService.list(page, size));
    }

    @ApiOperation(value = "Delete", notes = "Method used for delete profile",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/adm/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable("id") String id,
                                    HttpServletRequest request) {
        log.info("DELETE {}", id);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(profileService.delete(id));
    }
}
