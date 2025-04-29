package com.example.auth_service.controller;

import com.example.auth_service.dto.request.UserLoginRequestDTO;
import com.example.auth_service.dto.request.UserRegistrationRequestDTO;
import com.example.auth_service.dto.response.UserLoginResponseDTO;
import com.example.auth_service.dto.response.UserRegistrationResponseDTO;
import com.example.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDTO> register(@RequestBody UserRegistrationRequestDTO userRegistrationRequestDTO) {
        UserRegistrationResponseDTO response = authService.register(userRegistrationRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        UserLoginResponseDTO response = authService.login(userLoginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        authService.test();
        return ResponseEntity.ok("Auth service is up and running");
    }

}
