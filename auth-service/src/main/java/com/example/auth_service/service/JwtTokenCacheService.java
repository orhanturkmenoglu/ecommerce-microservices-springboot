package com.example.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.security.access.token.expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${spring.security.refresh.token.expiration}")
    private long REFRESH_EXPIRATION;

    @Value("${spring.security.verification.code.ttl.minutes}")
    private long VERIFICATION_CODE_TTL_MINUTES;

    public void storeAccessToken(String token, String email) {
        log.info("Storing access token {} to email {}", token, email);
        redisTemplate.opsForValue().set("access:" + email, token, ACCESS_EXPIRATION, TimeUnit.SECONDS);
    }

    public void storeRefreshToken(String token, String email) {
        log.info("Storing refresh token {} to email {}", token, email);
        redisTemplate.opsForValue().set("refresh:" + email, token, REFRESH_EXPIRATION, TimeUnit.SECONDS);
    }

    public void storeVerificationCodeInRedis(String email, String verificationCode) {
        log.info("Storing verification code {} for email {}", verificationCode, email);
        redisTemplate.opsForValue().set("verificationCode:" + email, verificationCode, Duration.ofMinutes(VERIFICATION_CODE_TTL_MINUTES));
    }

    public boolean isVerificationCodeCached(String email, String verificationCode) {
        String key = "verificationCode:" + email;
        log.info("Checking if verification code {} is cached for email {}", verificationCode, email);

        String cachedCode = redisTemplate.opsForValue().get(key);
        return verificationCode.equalsIgnoreCase(cachedCode);
    }

    public String getAccessToken(String email) {
        log.info("Getting access token for email {}", email);
        return redisTemplate.opsForValue().get("access:" + email);
    }

    public String getRefreshToken(String email) {
        log.info("Getting refresh token for email {}", email);
        return redisTemplate.opsForValue().get("refresh:" + email);
    }

    public void addToBlacklist(String token) {
        log.info("Adding token {} to blacklist", token);
        redisTemplate.opsForSet().add("BLACKLISTED", token);
    }

    public boolean isTokenBlackListed(String token) {
        log.info("Checking if token {} is blacklisted", token);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("BLACKLISTED", token));
    }

    public boolean isTokenValid(String token, String email) {
        String cachedAccessToken = getAccessToken(email);
        log.info("Checking if token {} is valid for email {}", token, email);

        if (cachedAccessToken == null || !cachedAccessToken.equals(token)) {
            return false;
        }

        Long expire = redisTemplate.getExpire("access:" + email, TimeUnit.SECONDS);
        log.info("expire: {}", expire);

        return expire != null && expire > 0 && !isTokenBlackListed(token);
    }

    public boolean isRefreshTokenValid(String token, String email) {
        String cachedRefreshToken = getRefreshToken(email);
        log.info("Checking if refresh token {} is valid for email {}", token, email);
        if (cachedRefreshToken == null || !cachedRefreshToken.equals(token)) {
            return false;
        }

        Long expire = redisTemplate.getExpire("refresh:" + email, TimeUnit.SECONDS);
        log.info("Refresh expire: {}", expire);
        return expire != null && expire > 0 && isTokenBlackListed(token);
    }

    public void deleteAccessToken(String email) {
        log.info("Deleting access token for email {}", email);
        redisTemplate.delete("access:" + email);
    }

    public void deleteRefreshToken(String email) {
        log.info("Deleting refresh token for email {}", email);
        redisTemplate.delete("refresh:" + email);
    }


    public void deleteAllTokens(String email) {
        log.info("Deleting all tokens for email {}", email);
        redisTemplate.delete("access:" + email);
        redisTemplate.delete("refresh:" + email);
    }

    public void invalidateOldTokenAndStoreNew(String oldToken, String newAccessToken, String newRefreshToken, String email) {
        addToBlacklist(oldToken);

        deleteAccessToken(email);

        deleteRefreshToken(email);

        storeAccessToken(newAccessToken, email);

        storeRefreshToken(newRefreshToken, email);

    }


    public boolean isTokenInCache(String email) {
        log.info("Checking if token {} is in cache", email);
        return Boolean.TRUE.equals(redisTemplate.hasKey("access:" + email)) ||
                Boolean.TRUE.equals(redisTemplate.hasKey("refresh:" + email));
    }


}
