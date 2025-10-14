package com.example.tourtravelserver.repository;

import com.example.tourtravelserver.entity.User;
import com.example.tourtravelserver.entity.UserToken;
import com.example.tourtravelserver.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IUserTokenRepository extends JpaRepository<UserToken, Long> {
    List<UserToken> findAllByUserAndStatusTrue(User user);

    List<UserToken> findAllByUserAndTokenTypeAndStatusIsTrue(User user, TokenType tokenType);

    Optional<UserToken> findByTokenHash(String token);

    @Query("SELECT ut FROM UserToken ut WHERE ut.user = :user AND ut.tokenType = :tokenType")
    Optional<UserToken> findByUserAndType(User user, TokenType tokenType);
}
