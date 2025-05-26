package com.user_service.service;


import com.user_service.entity.TokenEntity;
import com.user_service.entity.UserEntity;
import com.user_service.repo.TokenRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    @Autowired
    private TokenRepo repo;

    @Transactional
    public void saveToken(String token, UserEntity user) {
        TokenEntity tokenEntity = repo.findByUserId(user.getId());
        if(tokenEntity != null) {
            user.setToken(null);
            repo.deleteById(tokenEntity.getId());
            repo.flush();
        }
        tokenEntity = TokenEntity.builder()
                .token(token)
                .user(user)
                .build();
        repo.save(tokenEntity);
    }

    public void deleteToken(String token){
        repo.deleteByToken(token);
    }

    public boolean existToken(String token){
        return repo.existsByToken(token);
    }

    public void deleteByUserId(String userId){
        repo.deleteByUserId(userId);
    }
}
