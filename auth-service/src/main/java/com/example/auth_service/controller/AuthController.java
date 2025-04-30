package com.example.auth_service.controller;

import com.example.auth_service.dto.request.UserLoginRequestDTO;
import com.example.auth_service.dto.request.UserRegistrationRequestDTO;
import com.example.auth_service.dto.response.UserLoginResponseDTO;
import com.example.auth_service.dto.response.UserRegistrationResponseDTO;
import com.example.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDTO> register(@Valid @RequestBody UserRegistrationRequestDTO userRegistrationRequestDTO) {
        UserRegistrationResponseDTO response = authService.register(userRegistrationRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        UserLoginResponseDTO response = authService.login(userLoginRequestDTO);
        return ResponseEntity.ok(response);
    }

}
