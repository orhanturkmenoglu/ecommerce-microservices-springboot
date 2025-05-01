package com.example.auth_service.controller;

import com.example.auth_service.model.Token;
import com.example.auth_service.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenRepository tokenRepository;

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String tokenId) {
        boolean isValid = tokenRepository.findByAccessToken(tokenId)
                .map(Token::isActive)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        return ResponseEntity.ok(isValid);
    }
}
