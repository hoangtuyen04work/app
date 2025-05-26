//package com.user_service.configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.user_service.dto.ApiResponse;
//import com.user_service.exception.ErrorCode;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        ErrorCode errorCode = ErrorCode.NOT_AUTHENTICATION;
//        response.setStatus(errorCode.getStatus());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        ApiResponse<?> apiResponse = ApiResponse.builder()
//                .status(errorCode.getStatus())
//                .message(errorCode.getMessage())
//                .build();
//        ObjectMapper objectMapper = new ObjectMapper();
//        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
//        response.flushBuffer();
//    }
//}
