package com.example.backend.controller;

import com.example.backend.dto.request.AuthLoginRequest;
import com.example.backend.dto.request.AuthRefreshRequest;
import com.example.backend.dto.request.AuthRegisterRequest;
import com.example.backend.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody AuthRegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public Map<String, Object> refresh(@RequestBody AuthRefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        return authService.logout();
    }

    @GetMapping("/session")
    public Map<String, Object> session() {
        return authService.session();
    }
}
