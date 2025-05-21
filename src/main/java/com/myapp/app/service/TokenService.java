package com.myapp.app.service;

import com.myapp.app.entity.TokenEntity;
import com.myapp.app.entity.UserEntity;
import com.myapp.app.exception.AppException;
import com.myapp.app.repo.TokenRepo;
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
    public void saveToken(String token, UserEntity user) throws AppException {
        TokenEntity tokenEntity = repo.findByUserId(user.getId());
        if(tokenEntity != null) {
            user.setToken(null); // Xóa tham chiếu từ UserEntity
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
