package com.user_service.controller;


import com.commons.commonscore.dto.ApiResponse;
import com.commons.commonscore.dto.response.UserResponse;
import com.nimbusds.jose.JOSEException;
import com.user_service.dto.request.UserCreationRequest;
import com.user_service.dto.request.UserLoginRequest;
import com.user_service.dto.response.OB;
import com.commons.commonscore.exception.AppException;
import com.user_service.service.TokenService;
import com.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    @Transactional
    public ApiResponse<String> login(@RequestBody UserLoginRequest request)
            throws AppException, JOSEException {
        return ApiResponse.<String>builder()
                .data(userService.login(request))
                .build();
    }

    @PostMapping("/signup")
    @Transactional
    public ApiResponse<String> signup(@RequestBody UserCreationRequest request)
            throws JOSEException, AppException {
        return ApiResponse.<String>builder()
                .data(userService.signup(request))
                .build();
    }

    @PutMapping("/logoutt")
    public ApiResponse<Boolean> logout(@RequestBody String token) {
        return ApiResponse.<Boolean>builder()
                .data(userService.logout(token))
                .build();
    }

    @GetMapping("/info/token")
    public ApiResponse<UserResponse> getUserInfoByToken(@RequestParam String token) throws AppException, ParseException {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getUserInfoByToken(token))
                .build();
    }

    @GetMapping("/user")
    public ApiResponse<UserResponse> getUserInfo() throws AppException {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getUserInfo())
                .build();
    }

    @GetMapping("/info/user/{id}")
    public ApiResponse<UserResponse> info(@PathVariable("id") String id) throws AppException {
        var result = userService.getUserInfoById(id);
        return ApiResponse.<UserResponse>builder()
                .data(result)
                .build();
    }

    @GetMapping(value = "/test")
    public OB info() {
        return OB.builder()
                .message("test")
                .build();
    }
    @GetMapping("/auth")
    public boolean auth(@RequestParam String token) throws ParseException, JOSEException {
        return tokenService.authenticate(token);
    }
}
