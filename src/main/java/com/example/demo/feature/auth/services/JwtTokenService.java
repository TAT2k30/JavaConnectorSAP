package com.example.demo.feature.auth.services;

import com.example.demo.feature.account.domain.entities.Account;
import com.example.demo.feature.auth.domain.dto.request.LoginRequest;
import com.example.demo.feature.auth.domain.dto.request.RefreshTokenRequest;
import com.example.demo.feature.auth.domain.dto.response.JwtResponse;
import com.sap.conn.jco.JCoDestination;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;

public interface JwtTokenService {
    JCoDestination handleCreateJcoConnection(LoginRequest loginRequest) throws Exception;
    JwtResponse handleCreateJwtToken(Account account);
    JwtResponse handleRefreshToken(RefreshTokenRequest refreshTokenRequest);
    String resolveToken (HttpServletRequest httpServletRequest);
    Claims resolveClaims (String token, SecretKey secretKey);

}
