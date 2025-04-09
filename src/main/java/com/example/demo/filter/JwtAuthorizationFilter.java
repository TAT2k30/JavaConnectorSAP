package com.example.demo.filter;

import com.example.demo.common.ResponseHandler;
import com.example.demo.feature.auth.services.impl.AuthServiceImpl;
import com.example.demo.util.JWT.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final ObjectMapper mapper;
    private final AuthServiceImpl authService;
    private final JwtProperties jwtProperties;
    public JwtAuthorizationFilter(JwtProperties jwtProperties, ObjectMapper mapper, AuthServiceImpl authService) {
        this.mapper = mapper;
        this.authService = authService;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String accessToken = authService.resolveToken(request);

            if(accessToken != null) {
                Claims claims = authService.resolveClaims(accessToken, getSecretKey());
                if(claims != null) {
                    String user = claims.getSubject(); // Lấy giá trị "sub" từ JWT

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, Collections.emptyList());

                    // Đặt vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }catch (Exception e){
            handleException(response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
        errorDetails.put("message", e.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), errorDetails);
    }
    private SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(decodedKey);
    }
}
