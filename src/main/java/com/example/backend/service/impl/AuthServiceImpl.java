package com.example.backend.service.impl;

import com.example.backend.controller.ApiResponse;
import com.example.backend.dto.request.AuthLoginRequest;
import com.example.backend.dto.request.AuthRefreshRequest;
import com.example.backend.dto.request.AuthRegisterRequest;
import com.example.backend.entity.User;
import com.example.backend.service.AuthService;
import com.example.backend.service.PasswordHasher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final long REFRESH_TOKEN_TTL_SECONDS = 60L * 60 * 24 * 7;

    @PersistenceContext
    private EntityManager entityManager;

    private final ConcurrentMap<String, RefreshTokenState> refreshTokens = new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> register(AuthRegisterRequest request) {
        return ApiResponse.badRequest("Registration is not available for passengers. Only system administrators can create accounts.");
    }

    @Override
    public Map<String, Object> login(AuthLoginRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            return ApiResponse.badRequest("Email is required");
        }
        if (request.password() == null || request.password().isBlank()) {
            return ApiResponse.badRequest("Password is required");
        }

        User user = findUserByEmail(request.email());
        if (user == null || !PasswordHasher.matches(request.password(), user.getPasswordHash())) {
            return ApiResponse.badRequest("Invalid email or password");
        }

        if (user.getUserType() == User.UserType.PASSENGER) {
            return ApiResponse.badRequest("Passengers cannot login. Please contact an administrator.");
        }

        String accessToken = generateAccessToken(user);
        String refreshToken = issueRefreshToken(user);
        return ApiResponse.ok("Login successful", Map.of(
                "user", userData(user),
                "authenticated", true,
                "token", accessToken,
                "refreshToken", refreshToken,
                "tokenType", "Bearer",
                "expiresIn", 3600
        ));
    }

    @Override
    public Map<String, Object> refresh(AuthRefreshRequest request) {
        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            return ApiResponse.badRequest("Refresh token is required");
        }

        String incomingRefreshToken = request.refreshToken().trim();
        RefreshTokenState state = refreshTokens.get(incomingRefreshToken);
        if (state == null || state.expiresAt().isBefore(Instant.now())) {
            refreshTokens.remove(incomingRefreshToken);
            return ApiResponse.badRequest("Refresh token is invalid or expired");
        }

        User user = entityManager.find(User.class, state.userId());
        if (user == null || user.getStatus() != User.UserStatus.Active || user.getUserType() != User.UserType.SYSTEM_USER) {
            refreshTokens.remove(incomingRefreshToken);
            return ApiResponse.badRequest("User is not eligible for token refresh");
        }

        refreshTokens.remove(incomingRefreshToken);
        String newAccessToken = generateAccessToken(user);
        String newRefreshToken = issueRefreshToken(user);

        return ApiResponse.ok("Token refreshed", Map.of(
                "authenticated", true,
                "token", newAccessToken,
                "refreshToken", newRefreshToken,
                "tokenType", "Bearer",
                "expiresIn", 3600,
                "user", userData(user)
        ));
    }

    @Override
    public Map<String, Object> logout() {
        return ApiResponse.ok("Logout successful", Map.of("authenticated", false));
    }

    @Override
    public Map<String, Object> session() {
        User user = currentUser();
        if (user == null) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("authenticated", false);
            data.put("user", null);
            return ApiResponse.ok("Session retrieved", data);
        }
        return ApiResponse.ok("Session retrieved", Map.of(
                "authenticated", true,
                "user", userData(user),
                "token", generateAccessToken(user)
        ));
    }

    private User findUserByEmail(String email) {
        List<User> users = entityManager.createQuery("select u from User u where lower(u.email) = :email", User.class)
                .setParameter("email", email.trim().toLowerCase(Locale.ROOT))
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    private User currentUser() {
        List<User> users = entityManager.createQuery("select u from User u where u.status = :status order by u.updatedAt desc, u.id desc", User.class)
                .setParameter("status", User.UserStatus.Active)
                .getResultList();
        if (!users.isEmpty()) {
            return users.get(0);
        }
        List<User> allUsers = entityManager.createQuery("select u from User u order by u.updatedAt desc, u.id desc", User.class)
                .getResultList();
        return allUsers.isEmpty() ? null : allUsers.get(0);
    }

    private Map<String, Object> userData(User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("roleId", user.getRole() == null ? null : user.getRole().getId());
        data.put("roleName", user.getRole() == null ? null : user.getRole().getName());
        data.put("userType", user.getUserType());
        data.put("status", user.getStatus());
        data.put("createdAt", user.getCreatedAt());
        data.put("updatedAt", user.getUpdatedAt());
        return data;
    }

    private String generateAccessToken(User user) {
        return PasswordHasher.sha256(user.getEmail() + ":" + user.getId() + ":" + Instant.now().toEpochMilli());
    }

    private String issueRefreshToken(User user) {
        String refreshToken = PasswordHasher.sha256("refresh:" + user.getEmail() + ":" + user.getId() + ":" + Instant.now().toEpochMilli() + ":" + Math.random());
        refreshTokens.put(refreshToken, new RefreshTokenState(user.getId(), Instant.now().plusSeconds(REFRESH_TOKEN_TTL_SECONDS)));
        return refreshToken;
    }

    private record RefreshTokenState(Integer userId, Instant expiresAt) {
    }
}

