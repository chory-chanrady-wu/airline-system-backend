package com.example.backend.controller;
import com.example.backend.service.PasswordHasher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> payload) {
        PasswordHasher.sha256(payload.getOrDefault("password", "").toString());
        return ApiResponse.created("User registered successfully", Map.of(
                "email", payload.getOrDefault("email", ""),
                "name", payload.getOrDefault("name", "")
        ));
    }
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> payload) {
        PasswordHasher.sha256(payload.getOrDefault("password", "").toString());
        return ApiResponse.ok("Login successful", Map.of(
                "email", payload.getOrDefault("email", ""),
                "token", "demo-token"
        ));
    }
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        return ApiResponse.ok("Logout successful");
    }
    @GetMapping("/session")
    public Map<String, Object> session() {
        return ApiResponse.ok("Session retrieved", Map.of(
                "authenticated", true,
                "user", Map.of("id", 1, "email", "demo@example.com")
        ));
    }
}