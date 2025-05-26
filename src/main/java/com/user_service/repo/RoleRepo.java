package com.user_service.repo;

import com.user_service.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<RoleEntity, String> {
    boolean existsByRoleName(String name);
    Optional<RoleEntity> findByRoleName(String s);
}