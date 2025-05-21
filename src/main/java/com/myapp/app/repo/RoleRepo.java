package com.myapp.app.repo;

import com.myapp.app.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<RoleEntity, String> {
    boolean existsByRoleName(String name);
    Optional<RoleEntity> findByRoleName(String s);
}