package com.example.backend.controller;

import com.example.backend.dto.request.AuthLoginRequest;
import com.example.backend.dto.request.AuthRegisterRequest;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.EntityLookupSupport;
import com.example.backend.service.PasswordHasher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Transactional
public class AuthController {

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody AuthRegisterRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            return ApiResponse.badRequest("Name is required");
        }
        if (request.email() == null || request.email().isBlank()) {
            return ApiResponse.badRequest("Email is required");
        }
        if (request.password() == null || request.password().isBlank()) {
            return ApiResponse.badRequest("Password is required");
        }
        if (emailExists(request.email())) {
            return ApiResponse.badRequest("Email already exists");
        }

        Role role = defaultPassengerRole();
        if (role == null) {
            return ApiResponse.badRequest("No role is available for registration");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(PasswordHasher.sha256(request.password()))
                .role(role)
                .status(User.UserStatus.Active)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        return ApiResponse.created("User registered successfully", Map.of(
                "user", userData(user),
                "authenticated", true,
                "token", generateToken(user)
        ));
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthLoginRequest request) {
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

        return ApiResponse.ok("Login successful", Map.of(
                "user", userData(user),
                "authenticated", true,
                "token", generateToken(user)
        ));
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        return ApiResponse.ok("Logout successful", Map.of("authenticated", false));
    }

    @GetMapping("/session")
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
                "token", generateToken(user)
        ));
    }

    private User findUserByEmail(String email) {
        List<User> users = entityManager.createQuery("select u from User u where lower(u.email) = :email", User.class)
                .setParameter("email", email.trim().toLowerCase(Locale.ROOT))
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    private boolean emailExists(String email) {
        Long count = entityManager.createQuery("select count(u) from User u where lower(u.email) = :email", Long.class)
                .setParameter("email", email.trim().toLowerCase(Locale.ROOT))
                .getSingleResult();
        return count != null && count > 0;
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

    private Role defaultPassengerRole() {
        List<Role> roles = entityManager.createQuery("select r from Role r where lower(r.name) = :name", Role.class)
                .setParameter("name", "passenger")
                .getResultList();
        if (!roles.isEmpty()) {
            return roles.get(0);
        }
        List<Role> allRoles = entityManager.createQuery("select r from Role r order by r.id asc", Role.class)
                .getResultList();
        return allRoles.isEmpty() ? null : allRoles.get(0);
    }

    private Map<String, Object> userData(User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("roleId", user.getRole() == null ? null : user.getRole().getId());
        data.put("roleName", user.getRole() == null ? null : user.getRole().getName());
        data.put("status", user.getStatus());
        data.put("createdAt", user.getCreatedAt());
        data.put("updatedAt", user.getUpdatedAt());
        return data;
    }

    private String generateToken(User user) {
        return PasswordHasher.sha256(user.getEmail() + ":" + user.getId() + ":" + Instant.now().toEpochMilli());
    }
}