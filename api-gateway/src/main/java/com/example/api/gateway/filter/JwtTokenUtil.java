package com.example.api.gateway.filter;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
public class JwtTokenUtil {

    @Value("${spring.security.secret.key}")
    private String SECRET_KEY;


    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public Mono<Boolean> validateToken(final String token) {
        return Mono.fromCallable(() -> {
            try {
                Jws<Claims> claims = Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token);

                return true;
            } catch (Exception exception) {
                return false;
            }
        });
    }


}
