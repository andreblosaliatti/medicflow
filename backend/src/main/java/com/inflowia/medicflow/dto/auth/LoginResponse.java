package com.inflowia.medicflow.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Long id;
    private String login;
    private String nomeCompleto;
    private Long medicoId;
    private List<String> roles;
    private List<String> permissions;
    private String token;
}
