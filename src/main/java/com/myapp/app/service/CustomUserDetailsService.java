package com.myapp.app.service;

import com.myapp.app.entity.UserEntity;
import com.myapp.app.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;


    //id is subject in token
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserEntity user = userRepo.findById(userId).orElseThrow( () -> new UsernameNotFoundException(userId));
        return User.builder()
                .username(userId)
                .password(user.getPassword())
                .authorities(String.valueOf(user.getRoles()))
                .build();
    }


}
