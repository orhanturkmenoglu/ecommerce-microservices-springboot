package com.example.auth_service.controller;

import com.example.auth_service.service.SecretManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SecretManagerService secretManagerService;


    @GetMapping("/secret")
    public ResponseEntity<String> getSecret(@RequestParam String secretName) {
        return ResponseEntity.ok(secretManagerService.getSecret(secretName));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<String>> getAllSecrets() {
        return ResponseEntity.ok(secretManagerService.getAllSecrets());
    }

}
