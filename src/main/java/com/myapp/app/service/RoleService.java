package com.myapp.app.service;

import com.myapp.app.entity.RoleEntity;
import com.myapp.app.exception.AppException;
import com.myapp.app.exception.ErrorCode;
import com.myapp.app.repo.RoleRepo;
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