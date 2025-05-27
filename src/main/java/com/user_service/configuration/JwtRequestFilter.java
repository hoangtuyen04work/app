package com.user_service.configuration;

import com.nimbusds.jose.JOSEException;
import com.commons.commons_security.TokenUtils;
import com.commons.commonscore.exception.AppException;
import com.commons.commonscore.exception.ErrorCode;
import com.user_service.service.CustomUserDetailsService;
import com.user_service.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String userId = null;
        String jwt = null;
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.replace("Bearer ", "");
            try {
                userId = tokenUtils.getUserIdByToken(jwt);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if(jwt != null && !tokenService.existToken(jwt))
            try {
                throw new AppException(ErrorCode.NOT_AUTHENTICATION);
            } catch (AppException e) {
                throw new RuntimeException(e);
            }
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = customUserDetailsService.loadUserByUsername(userId);
            try {
                if (tokenUtils.isValidToken(jwt)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (JOSEException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
