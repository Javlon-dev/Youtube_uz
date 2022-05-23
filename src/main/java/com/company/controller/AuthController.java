package com.company.controller;

import com.company.dto.AuthDTO;
import com.company.dto.RegistrationDTO;
import com.company.service.AuthService;
import com.company.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Api(tags = "Authorization")
public class AuthController {

    private final AuthService authService;


    @ApiOperation(value = "Login", notes = "Method used for login and getting token")
    @PostMapping("/login")
    public ResponseEntity<?> authProfile(@RequestBody @Valid AuthDTO dto) {
        log.info("Authorization: {}", dto);
        return ResponseEntity.ok(authService.login(dto));
    }

    @ApiOperation(value = "Registration", notes = "Method used for registration")
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid RegistrationDTO dto) {
        log.info("Registration: {}", dto);
        return ResponseEntity.ok(authService.registration(dto));
    }

    @ApiOperation(value = "Email Verification", notes = "Method used for email verifier")
    @GetMapping("/verification/{jwt}")
    public ResponseEntity<?> verification(@PathVariable("jwt") String jwt) {
        log.info("Verification: {}", jwt);
        return ResponseEntity.ok(authService.verification(JwtUtil.decodeAndGetId(jwt)));
    }

}
