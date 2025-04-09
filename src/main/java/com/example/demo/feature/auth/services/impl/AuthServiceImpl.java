package com.example.demo.feature.auth.services.impl;

import com.example.demo.feature.account.domain.entities.Account;
import com.example.demo.feature.auth.domain.dto.request.LoginRequest;
import com.example.demo.feature.auth.domain.dto.request.RefreshTokenRequest;
import com.example.demo.feature.auth.domain.dto.response.JwtResponse;
import com.example.demo.feature.auth.services.JwtTokenService;
import com.example.demo.handler.JcoDestinationFileHandler;
import com.example.demo.util.enums.AuthEnums;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Service
public class AuthServiceImpl implements JwtTokenService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jco.destination.name}")
    private String jcoDestinationName;
    private Key cachedKey;
    private final JcoDestinationFileHandler jcoDestinationFileHandler = new JcoDestinationFileHandler();
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";
    private final long EXPIRES_IN = 86400000; // 1 day
    private final long REFRESH_EXPIRES_IN = 172800000; // 2 days
    @Override
    public JCoDestination handleCreateJcoConnection(LoginRequest loginRequest) throws Exception {
        String filePath = jcoDestinationFileHandler.createDestinationFile(
                loginRequest.getUserName(),
                loginRequest.getUserPassword(),
                loginRequest.getClientCode()
        );

        File destinationFile = new File(filePath);
        if (!destinationFile.exists()) {
            throw new RuntimeException("Destination file not found: " + filePath);
        }

        return JCoDestinationManager.getDestination(jcoDestinationName);
    }

    @Override
    public JwtResponse handleCreateJwtToken(Account account) {

        String accessToken = Jwts.builder()
                .setSubject(account.getUser())
                .claim("sysId", account.getSysId())
                .claim("client", account.getClient())
                .claim("userName", account.getUser())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRES_IN))
                .signWith(SignatureAlgorithm.HS256, getKey())
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(account.getUser())
                .claim("sysId", account.getSysId())
                .claim("client", account.getClient())
                .claim("userName", account.getUser())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRES_IN))
                .signWith(SignatureAlgorithm.HS256, getKey())
                .compact();

        return new JwtResponse(accessToken, refreshToken, REFRESH_EXPIRES_IN, EXPIRES_IN, secretKey, account);
    }

    @Override
    public JwtResponse handleRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            Claims returnClaim = Jwts.parserBuilder()
                    .setSigningKey(this.getKey())
                    .build()
                    .parseClaimsJws(refreshTokenRequest.getRefreshToken()) // Giải mã Refresh Token
                    .getBody();
            // Check Refresh token có hết hạn hay chưa.
            Date expiration = returnClaim.getExpiration();
            if (expiration.before(new Date())) {
                throw new JwtException(AuthEnums.EXPIRED_REFRESH_TOKEN.getMessage());
            }
            // Check Refresh token có trả ra đúng account trong phiên đăng nhập đó không
            String systemId = returnClaim.get("sysId", String.class);
            String client = returnClaim.get("client", String.class);
            String userName = returnClaim.get("userName", String.class);
            if(
                    !Objects.equals(systemId, refreshTokenRequest.getAccount().getSysId()) ||
                    !Objects.equals(client, refreshTokenRequest.getAccount().getClient()) ||
                    !Objects.equals(userName, refreshTokenRequest.getAccount().getUser())
            ) {
                    throw new RuntimeException(AuthEnums.INVALID_SESSION_ACCOUNT.getMessage());
            }
            Account returnAccount = new Account();
            returnAccount.setSysId(systemId);
            returnAccount.setClient(client);
            returnAccount.setUser(userName);
            return handleCreateJwtToken(returnAccount);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException(AuthEnums.INVALID_JWT_TOKEN_CLAIMS.getMessage(), e);
        }
    }

    @Override
    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader(TOKEN_HEADER);
        if(bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)){
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    @Override
    public Claims resolveClaims(String token, SecretKey secretKey) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException(AuthEnums.INVALID_JWT_TOKEN_CLAIMS.getMessage(), e);
        }
    }

    private Key getKey() {
        if (cachedKey == null) {
            byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
            cachedKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return cachedKey;
    }
}
