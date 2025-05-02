package com.example.api.gateway.filter;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    @Value("${spring.security.secret.key}")
    private String SECRET_KEY;

    private final ReactiveStringRedisTemplate redisTemplate;


    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public Mono<Boolean> validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject(); // ya da token'a koyduğun başka bir kullanıcı bilgisi

            // Redis'te blacklist kontrolü:
            return redisTemplate.hasKey("BLACKLISTED:" + token)
                    .flatMap(isBlacklisted -> {
                        if (Boolean.TRUE.equals(isBlacklisted)) {
                            return Mono.just(false); // blacklistteyse geçersiz
                        }

                        // Access token kontrolü (isteğe bağlı)
                        return redisTemplate.opsForValue()
                                .get("access:" + email)
                                .map(redisToken -> token.equals(redisToken));
                    });
        } catch (Exception e) {
            return Mono.just(false);
        }
    }


}
