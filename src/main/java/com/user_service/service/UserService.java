package com.user_service.service;


import com.commons.commons_client.utils.UserToPostClient;
import com.commons.commons_security.TokenUtils;
import com.commons.commonscore.dto.response.UserResponse;
import com.commons.commonscore.exception.AppException;
import com.commons.commonscore.exception.ErrorCode;
import com.nimbusds.jose.JOSEException;
import com.user_service.dto.request.UserCreationRequest;
import com.user_service.dto.request.UserLoginRequest;
import com.user_service.entity.RoleEntity;
import com.user_service.entity.UserEntity;

import com.user_service.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepo repo;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserToPostClient userToPostClient;

    @Transactional
    public String signup(UserCreationRequest request) throws JOSEException, AppException {
        if(repo.existsByEmail(request.getEmail())
                || repo.existsByPhone(request.getPhone())
                || repo.existsByUserName(request.getUserName()))
            throw new AppException(ErrorCode.USER_EXISTED);
        UserEntity user = UserEntity.builder()
                .dob(request.getDob())
                .email(request.getEmail())
                .phone(request.getPhone())
                .userName(request.getUserName())
                .address(request.getAddress())
                .roles(List.of(roleService.getRoleByRoleName("USER")))
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        user = repo.save(user);
        userToPostClient.info(user.getId());
        String token = tokenUtils.generateToken(user.getId(), buildRoles(user.getRoles()));
        tokenService.saveToken(token, user);
        return token;
    }

    private List<String> buildRoles(List<RoleEntity> roles){
        List<String> result = new ArrayList<>();
        for(RoleEntity role : roles){
            result.add(role.getRoleName());
        }
        return result;
    }

    public String login(UserLoginRequest request) throws AppException, JOSEException {
        UserEntity user;
        if(request.getEmail() != null && !request.getEmail().isBlank()) {
            user = repo.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }
        else if(request.getPhone() != null && !request.getPhone().isBlank()) {
            user = repo.findByPhone(request.getPhone())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }
        else if(request.getUsername() != null && !request.getUsername().isBlank()) {
            user = repo.findByUserName(request.getUsername())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }
        else{
            throw new AppException(ErrorCode.CONFLICT);
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw  new AppException(ErrorCode.WRONG_PASSWORD_OR_USERID);
        String token = tokenUtils.generateToken(user.getId(), buildRoles(user.getRoles()));
        tokenService.saveToken(token, user);
        return token;
    }

    public boolean logout(String token) {
        tokenService.deleteToken(token);
        return true;
    }

    public UserEntity save(UserEntity user){
        return repo.save(user);
    }

    public UserResponse getUserInfoById(String userId) throws AppException {
        UserEntity user = repo.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserResponse.builder()
                .userName(user.getUserName())
                .dob(user.getDob())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .id(user.getId())
                .build();
    }


    public UserResponse getUserInfo() throws AppException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = repo.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserResponse.builder()
                .userName(user.getUserName())
                .dob(user.getDob())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .id(user.getId())
                .build();
    }

    public UserResponse getUserInfoByToken(String token) throws AppException, ParseException {
        String userId = tokenUtils.getUserIdByToken(token);
        UserEntity user = repo.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserResponse.builder()
                .userName(user.getUserName())
                .dob(user.getDob())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .id(user.getId())
                .build();
    }

}
