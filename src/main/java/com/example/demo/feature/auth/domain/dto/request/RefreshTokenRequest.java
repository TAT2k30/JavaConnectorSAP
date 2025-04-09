package com.example.demo.feature.auth.domain.dto.request;

import com.example.demo.feature.account.domain.entities.Account;
import com.example.demo.feature.auth.domain.dto.response.JwtResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {
    private Account account;
    private String refreshToken;
}
