package com.example.auth_service.service;

import com.example.auth_service.dto.request.UserLoginRequestDTO;
import com.example.auth_service.dto.request.UserRegistrationRequestDTO;
import com.example.auth_service.dto.response.UserLoginResponseDTO;
import com.example.auth_service.dto.response.UserRegistrationResponseDTO;
import com.example.auth_service.mapper.UserMapper;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.utils.JwtTokenUtil;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;


    public UserRegistrationResponseDTO register(UserRegistrationRequestDTO userRegistrationRequestDTO) {

        User user = UserMapper.mapToUser(userRegistrationRequestDTO);
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDTO.getPassword()));

        log.info("User : {}", user);

        User savedUser = userRepository.save(user);

        log.info("User saved : {}", savedUser);

        return new UserRegistrationResponseDTO("User registered successfully : " + savedUser.getEmail());
    }


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

        SecurityContextHolder.getContext().setAuthentication(authenticated);

        User user = userRepository.findByEmail(userLoginRequestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        log.info("User found : {}", user);

        String accessToken = jwtTokenUtil.generateAccessToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        log.info("Access token : {}", accessToken);
        log.info("Refresh token : {}", refreshToken);

        return new UserLoginResponseDTO(accessToken, refreshToken);
    }


    public String test (){
        return "test";
    }

}
