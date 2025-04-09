package com.example.demo.feature.account.domain.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String user;
    private String sysId;
    private String client;
    private String language;
}
