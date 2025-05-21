package com.myapp.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myapp.app.dto.ApiResponse;
import com.myapp.app.dto.request.UserCreationRequest;
import com.myapp.app.dto.request.UserLoginRequest;
import com.myapp.app.dto.response.UserResponse;
import com.myapp.app.exception.AppException;
import com.myapp.app.service.UserService;
import com.nimbusds.jose.JOSEException;
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

    @PutMapping("/logout")
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

    @GetMapping("/info")
    public ApiResponse<UserResponse> getUserInfo() throws AppException {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getUserInfo())
                .build();
    }
}
