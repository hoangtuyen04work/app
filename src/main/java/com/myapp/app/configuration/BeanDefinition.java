package com.myapp.app.configuration;

import com.utils.token.TokenUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanDefinition {
    @Bean
    public TokenUtils myService() {
        return new TokenUtils(); // Tùy vào constructor, truyền tham số nếu cần
    }
}
