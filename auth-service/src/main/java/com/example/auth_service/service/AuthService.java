package com.example.auth_service.service;

import com.example.auth_service.dto.request.UserEmailVerificationRequestDto;
import com.example.auth_service.dto.request.UserLoginRequestDTO;
import com.example.auth_service.dto.request.UserRegistrationRequestDTO;
import com.example.auth_service.dto.request.UserUpdatePasswordRequestDTO;
import com.example.auth_service.dto.response.UserEmailVerificationResponseDto;
import com.example.auth_service.dto.response.UserLoginResponseDTO;
import com.example.auth_service.dto.response.UserRegistrationResponseDTO;
import com.example.auth_service.dto.response.UserUpdatePasswordResponseDTO;
import com.example.auth_service.exception.*;
import com.example.auth_service.mapper.UserMapper;
import com.example.auth_service.model.Token;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.TokenRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenRepository tokenRepository;
    private final JwtTokenCacheService cacheService;
    private final EmailService emailService;

    @Transactional
    public UserRegistrationResponseDTO register(UserRegistrationRequestDTO userRegistrationRequestDTO) {

        if (userRepository.existsByEmail(userRegistrationRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("User with email " + userRegistrationRequestDTO.getEmail() + " already exists");
        }

        User user = UserMapper.mapToUser(userRegistrationRequestDTO);
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDTO.getPassword()));

        String verificationCode = generateVerificationCode();
        log.info("Verification code : {}", verificationCode);

        User savedUser = userRepository.save(user);

        log.info("User saved : {}", savedUser);

        try {
            emailService.sendEmailVerification(savedUser.getEmail(), verificationCode);
        } catch (Exception e) {
            throw new EmailSendException("Failed to send email verification");
        }

        cacheService.storeVerificationCodeInRedis(savedUser.getEmail(), verificationCode);

        return new UserRegistrationResponseDTO("User registered successfully : " + savedUser.getEmail());
    }


    public UserEmailVerificationResponseDto verifyEmail(UserEmailVerificationRequestDto userEmailVerificationRequestDto) {

        User user = userRepository.findByEmail(userEmailVerificationRequestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new EmailAlreadyExistsException("User with email " + userEmailVerificationRequestDto.getEmail() + " already verified");
        }

        boolean isVerificationCodeCached = cacheService.isVerificationCodeCached(
                (userEmailVerificationRequestDto.getEmail()));


        if (!isVerificationCodeCached) {
            throw new InvalidVerificationCodeException("Invalid verification code");
        }

        user.setEmailVerified(true);
        userRepository.save(user);

        return new UserEmailVerificationResponseDto("Email verified successfully");
    }

    public void resendVerificationCode(String email) {

        if (email == null) {
            throw new NullPointerException("Email cannot be null");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));

        if (user.isEmailVerified()) {
            throw new EmailAlreadyExistsException("Email already verified");
        }

        boolean isVerificationCodeExpired = cacheService.isVerificationCodeExpired(user.getEmail());

        if (isVerificationCodeExpired) {
            throw new VerificationCodeStillValidException("Current verification code is still valid. Please wait or use the existing code.");
        }

        String newVerificationCode = generateVerificationCode();

        cacheService.storeVerificationCodeInRedis(
                user.getEmail(),
                newVerificationCode
        );

        try {
            emailService.sendEmailVerification(user.getEmail(), newVerificationCode);
        } catch (Exception e) {
            throw new EmailSendException("Failed to send email verification");
        }
    }


    @Transactional
    public UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO) {

        if (userLoginRequestDTO.getEmail() == null || userLoginRequestDTO.getPassword() == null) {
            throw new NullPointerException("Email and password cannot be null");
        }


        Authentication authenticated = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequestDTO.getEmail(),
                        userLoginRequestDTO.getPassword()
                ));

        log.info("Authenticated : {}", authenticated);

        if (!authenticated.isAuthenticated()) {
            throw new BadCredentialsException("Bad credentials");
        }

        User user = userRepository.findByEmail(userLoginRequestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setLoggedOut(true);

        log.info("login:: User found : {}", user);

        boolean isVerificationCodeCached = cacheService.isVerificationCodeCached(user.getEmail());

        if (!isVerificationCodeCached) {
            throw new VerificationCodeExpiredException("Verification code expired. Please request a new one.");
        }

        if (!user.isEmailVerified()) {
            throw new InvalidVerificationCodeException("Email not verified");
        }


        List<Token> allAccessTokenByUser = tokenRepository.findAllTokenByUser(user.getId());

        if (!allAccessTokenByUser.isEmpty()) {
            allAccessTokenByUser.forEach(token -> token.setActive(false));
            tokenRepository.saveAll(allAccessTokenByUser);
        }


        String oldAccessToken = cacheService.getAccessToken(user.getEmail());
        String oldRefreshToken = cacheService.getRefreshToken(user.getEmail());

        log.info("oldAccessToken : {}", oldAccessToken);
        log.info("oldRefreshToken : {}", oldRefreshToken);

        String newAccessToken = jwtTokenUtil.generateAccessToken(user);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(user);

        log.info("newAccessToken : {}", newAccessToken);
        log.info("newRefreshToken : {}", newRefreshToken);

        if (oldAccessToken != null && oldRefreshToken != null) {
            cacheService.invalidateOldTokenAndStoreNew(oldAccessToken,
                    newAccessToken, newRefreshToken, user.getEmail());
        }

        cacheService.storeAccessToken(newAccessToken, user.getEmail());
        cacheService.storeRefreshToken(newRefreshToken, user.getEmail());

        saveUserToken(newAccessToken, newRefreshToken, user);

        return new UserLoginResponseDTO(newAccessToken, newRefreshToken);
    }


    public UserLoginResponseDTO refreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            log.warn("Refresh token is null or empty");
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }

        String cleanToken = refreshToken.replace("Bearer ", "").trim();
        Token existingToken = tokenRepository.findByRefreshToken(cleanToken)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found in database");
                    return new BadCredentialsException("Invalid refresh token");
                });

        String userEmail = existingToken.getUser().getEmail();
        log.info("Refresh token found for user: {}", userEmail);

        boolean isValidInCache = cacheService.isRefreshTokenValid(cleanToken, userEmail);

        if (isValidInCache) {
            log.warn("Refresh token is not valid in cache for user: {}", userEmail);
            throw new BadCredentialsException("Invalid refresh token");
        }

        boolean tokenBlackListed = cacheService.isTokenBlackListed(refreshToken);

        if (tokenBlackListed) {
            log.warn("Refresh token is blacklisted for user: {}", userEmail);
            throw new BadCredentialsException("Invalid refresh token");
        }

        cacheService.invalidateOldTokenAndStoreNew(refreshToken,
                existingToken.getAccessToken(), existingToken.getRefreshToken(), userEmail);

        existingToken.setActive(false);
        tokenRepository.save(existingToken);
        log.info("Old refresh token deactivated for user: {}", userEmail);

        String newAccessToken = jwtTokenUtil.generateAccessToken(existingToken.getUser());
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(existingToken.getUser());

        cacheService.storeAccessToken(newAccessToken, userEmail);
        cacheService.storeRefreshToken(newRefreshToken, userEmail);

        saveUserToken(newAccessToken, newRefreshToken, existingToken.getUser());

        log.info("New tokens generated and saved successfully for user: {}", userEmail);

        return new UserLoginResponseDTO(newAccessToken, newRefreshToken);
    }

    public UserUpdatePasswordResponseDTO updatePassword(UserUpdatePasswordRequestDTO updatePasswordRequestDTO) {

        if (updatePasswordRequestDTO.getOldPassword() == null
                || updatePasswordRequestDTO.getNewPassword() == null
                || updatePasswordRequestDTO.getConfirmPassword() == null) {

            log.error("One or more required password fields are null: old={}, new={}, confirm={}",
                    updatePasswordRequestDTO.getOldPassword(),
                    updatePasswordRequestDTO.getNewPassword(),
                    updatePasswordRequestDTO.getConfirmPassword());

            throw new IllegalArgumentException("Password fields must not be null");

        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated()) {
            log.warn("Unauthenticated access attempt to password update");
            throw new BadCredentialsException("User is not authenticated");
        }

        String username = authentication.getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", username);
                    return new UsernameNotFoundException("User not found with email: " + username);
                });

        if (!passwordEncoder.matches(updatePasswordRequestDTO.getOldPassword(), user.getPassword())) {
            log.warn("Old password does not match for user: {}", username);
            throw new BadCredentialsException("Bad credentials for old password match check failed");
        }

        if (!updatePasswordRequestDTO.getNewPassword().equals(updatePasswordRequestDTO.getConfirmPassword())) {
            log.warn("New password and confirmation do not match for user: {}", username);
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordRequestDTO.getNewPassword()));
        userRepository.save(user);

        log.info("Password updated successfully for user: {}", username);

        return new UserUpdatePasswordResponseDTO("Password updated successfully");
    }

    public void logout(String token) {

        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        String refreshToken = token.replace("Bearer ", "").trim();

        String extractedUsername = jwtTokenUtil.extractUsername(refreshToken);

        User user = userRepository.findByEmail(extractedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + extractedUsername + " not found"));

        log.info("logout:: User found: {}", user);

        cacheService.deleteCacheVerificationCode(user.getEmail());

        user.setEmailVerified(false);
        user.setLoggedOut(true);

        List<Token> tokenList = tokenRepository.findAllTokenByUser(user.getId());

        if (!tokenList.isEmpty()) {

            tokenList.forEach(t -> t.setActive(false));

            tokenRepository.saveAll(tokenList);
            log.info("logout:: Inactivated {} tokens for user: {}", tokenList.size(), user);

            cacheService.addToBlacklist(refreshToken);
            log.info("logout:: Token added to blacklist: {}", extractedUsername);
        } else {
            log.warn("logout:: No active tokens found for user: {}", user);
        }
    }


    private String generateVerificationCode() {
        String randomUuid = UUID.randomUUID().toString().replace("-", "");
        randomUuid = randomUuid.toUpperCase(Locale.ROOT);
        return randomUuid.substring(0, 6);
    }


    private void saveUserToken(String accessToken, String refreshToken, User user) {

        Claims claims = jwtTokenUtil.getClaims(accessToken);
        String jti = claims.getId();

        log.info("claims : {}", claims);
        log.info("jti : {}", jti);

        Token token = new Token();
        token.setId(jti);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setActive(true);
        token.setUser(user);

        Token savedToken = tokenRepository.save(token);
        log.info("Token saved : {}", savedToken);
    }
}