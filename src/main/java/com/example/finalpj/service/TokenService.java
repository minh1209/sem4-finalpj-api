package com.example.finalpj.service;

import com.example.finalpj.entity.Token;
import com.example.finalpj.reposiroty.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
    public Optional<Token> findByUserId(String id) {
        return tokenRepository.findByUserId(id);
    }
    public Token save(Token t) { return tokenRepository.save(t); }
}
