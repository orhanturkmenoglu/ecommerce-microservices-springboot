package com.example.auth_service.controller;

import com.example.auth_service.dto.request.UserLoginRequestDTO;
import com.example.auth_service.dto.request.UserRegistrationRequestDTO;
import com.example.auth_service.dto.response.UserLoginResponseDTO;
import com.example.auth_service.dto.response.UserRegistrationResponseDTO;
import com.example.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Register a new user", description = "Register a new user by providing email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data.")
    })
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDTO> register(@Valid @RequestBody UserRegistrationRequestDTO userRegistrationRequestDTO) {
        UserRegistrationResponseDTO response = authService.register(userRegistrationRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User login", description = "Log in a user by providing email and password to receive access and refresh tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful."),
            @ApiResponse(responseCode = "400", description = "Invalid email or password.")
    })
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        UserLoginResponseDTO response = authService.login(userLoginRequestDTO);
        return ResponseEntity.ok(response);
    }

}
