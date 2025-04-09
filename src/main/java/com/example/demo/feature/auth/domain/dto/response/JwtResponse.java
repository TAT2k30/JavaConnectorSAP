package com.example.demo.feature.auth.domain.dto.response;

import com.example.demo.feature.account.domain.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private long expireRefreshToken;
    private long expires;
    private String secretKey;
    private Account account;
}
