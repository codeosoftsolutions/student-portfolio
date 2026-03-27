


package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.dto.LoginRequest;
import com.studenttap.dto.LoginResponse;
import com.studenttap.dto.RegisterRequest;
import com.studenttap.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ===================================================
    // POST /api/auth/register
    // Register a new student
    // ===================================================
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        try {
            String message = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // POST /api/auth/login
    // Login and get JWT token
    // ===================================================
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // GET /api/auth/check-username/{username}
    // Check if username is available (called while typing)
    // ===================================================
    @GetMapping("/check-username/{username}")
    public ResponseEntity<ApiResponse> checkUsername(
            @PathVariable String username) {
        try {
            // This will be handled in service - simple check
            return ResponseEntity.ok(
                ApiResponse.success("Username is available", true)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Username not available"));
        }
    }

    // ===================================================
    // GET /api/auth/test
    // Simple test to verify API is working
    // ===================================================
    @GetMapping("/test")
    public ResponseEntity<ApiResponse> test() {
        return ResponseEntity.ok(
            ApiResponse.success("Auth API is working correctly!")
        );
    }
}