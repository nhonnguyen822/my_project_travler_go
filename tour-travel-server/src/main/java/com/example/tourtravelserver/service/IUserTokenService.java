package com.example.tourtravelserver.service;

import com.example.tourtravelserver.entity.User;
import com.example.tourtravelserver.entity.UserToken;
import com.example.tourtravelserver.enums.TokenType;

import java.util.Optional;

public interface IUserTokenService {
    UserToken save(UserToken userToken);

    Optional<UserToken> findByToken(String token);

    String hashToken(String token);

    String generateToken(User user, TokenType tokenType);

    Optional<UserToken> findByUserAndType(User user, TokenType tokenType);
}
