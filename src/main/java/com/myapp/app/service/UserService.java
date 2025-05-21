package com.myapp.app.service;

import com.myapp.app.dto.request.UserCreationRequest;
import com.myapp.app.dto.request.UserLoginRequest;
import com.myapp.app.dto.response.UserResponse;
import com.myapp.app.entity.RoleEntity;
import com.myapp.app.entity.UserEntity;
import com.myapp.app.exception.AppException;
import com.myapp.app.exception.ErrorCode;
import com.myapp.app.repo.UserRepo;
import com.nimbusds.jose.JOSEException;
import com.utils.token.TokenUtils;
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
    final private TokenUtils tokenUtil;

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
