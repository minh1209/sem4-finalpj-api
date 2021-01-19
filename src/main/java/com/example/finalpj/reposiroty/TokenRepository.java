package com.example.finalpj.reposiroty;

import com.example.finalpj.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {
    Optional<Token> findByUserId(String id);
    Optional<Token> findByToken(String token);
}
