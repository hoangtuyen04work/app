package com.user_service.configuration;

import com.user_service.entity.RoleEntity;
import com.user_service.entity.UserEntity;
import com.user_service.repo.RoleRepo;
import com.user_service.repo.UserRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepo userRepo, RoleRepo roleRepo){
        return args->{
            List<RoleEntity> roles = new ArrayList<>();
            if(!roleRepo.existsByRoleName("ADMIN")){
                RoleEntity admin = RoleEntity.builder()
                        .roleName("ADMIN")
                        .build();
                roleRepo.save(admin);
                roles.add(admin);
            }
            if(!roleRepo.existsByRoleName("USER")){
                RoleEntity user = RoleEntity.builder()
                        .roleName("USER")
                        .build();
                roleRepo.save(user);
                roles.add(user);
            }
            if(!userRepo.existsByUserName("ADMIN")){
                UserEntity user = UserEntity
                                .builder()
                                .userName("ADMIN")
                                .password(passwordEncoder.encode("ADMIN"))
                                .build();
                userRepo.save(user);
            }
        };
    }
}