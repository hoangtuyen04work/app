package com.user_service.service;

import com.user_service.entity.RoleEntity;
import com.user_service.exception.AppException;
import com.user_service.exception.ErrorCode;
import com.user_service.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService  {
    @Autowired
    private RoleRepo roleRepo;

    public RoleEntity getRoleByRoleName(String name) throws AppException {
        return roleRepo.findByRoleName(name).orElseThrow(() -> new AppException((ErrorCode.CONFLICT)));
    }


}