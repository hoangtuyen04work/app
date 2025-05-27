package com.user_service.repo;


import com.user_service.entity.TokenEntity;
import com.user_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo extends JpaRepository<TokenEntity, String> {
    void deleteByToken(String token);
    boolean existsByToken(String token);
    TokenEntity findByUserId(String userId);

    void deleteByUserId(String userId);

    @Query("DELETE FROM TokenEntity t WHERE t.user = :user")
    int deleteByUser(@Param("user") UserEntity user);
}
