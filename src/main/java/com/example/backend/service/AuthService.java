package com.example.backend.service;

import com.example.backend.dto.request.AuthLoginRequest;
import com.example.backend.dto.request.AuthRefreshRequest;
import com.example.backend.dto.request.AuthRegisterRequest;

import java.util.Map;

public interface AuthService {
    Map<String, Object> register(AuthRegisterRequest request);

    Map<String, Object> login(AuthLoginRequest request);

    Map<String, Object> refresh(AuthRefreshRequest request);

    Map<String, Object> logout();

    Map<String, Object> session();
}

