package com.example.tourtravelserver.security.jwt.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
}

