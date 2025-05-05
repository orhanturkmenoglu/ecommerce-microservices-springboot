package com.example.auth_service.utils;


import com.example.auth_service.model.Token;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenUtil {

    private final TokenRepository tokenRepository;
    @Value("${spring.security.secret.key}")
    private String SECRET_KEY;

    @Value("${spring.security.access.token.expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${spring.security.refresh.token.expiration}")
    private long REFRESH_EXPIRATION;

    public JwtTokenUtil(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }


    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    private String generateToken(Map<String, Object> claims, User user, long expiration) {
        String roles = user.getRole().name();

        String jti = UUID.randomUUID().toString() ;
        claims.put("jti", jti);

        return Jwts.builder()
                .setIssuer("auth-service")
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, user, ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, user, REFRESH_EXPIRATION);
    }


    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isValidateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValidToken = tokenRepository.findByAccessToken(token)
                .map(Token::isActive)
                .orElse(false);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token) && isValidToken;
    }

    public boolean isValidateRefreshToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValidRefreshToken = tokenRepository.findByRefreshToken(token)
                .map(Token::isActive)
                .orElse(false);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token) && isValidRefreshToken;
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
