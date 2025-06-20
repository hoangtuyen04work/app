package com.user_service.configuration;

import com.commons.commons_security.TokenUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanDefinition {
    @Value("${jwt.signerKey}")
    private String signerKey;
    @Bean
    public TokenUtils myService() {
        return new TokenUtils(signerKey);
    }
}
