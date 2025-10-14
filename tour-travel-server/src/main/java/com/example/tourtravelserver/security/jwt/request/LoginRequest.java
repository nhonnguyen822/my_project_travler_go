package com.example.tourtravelserver.security.jwt.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
    private Boolean rememberMe = false;
}
