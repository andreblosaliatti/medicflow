package com.inflowia.medicflow.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String type;
    private Long userId;
    private String login;
    private String nomeCompleto;
    private List<String> roles;
}