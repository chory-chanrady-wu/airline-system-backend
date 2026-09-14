package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @GetMapping("/users/me")
    public Map<String, Object> getCurrentUser() {
        return ApiResponse.ok("Current user loaded", Map.of(
                "id", 1,
                "name", "Demo User",
                "email", "demo@example.com",
                "role", "admin"
        ));
    }

    @PatchMapping("/users/me")
    public Map<String, Object> updateCurrentUser(@RequestBody Map<String, Object> payload) {
        return ApiResponse.ok("Profile updated", Map.of(
                "id", 1,
                "name", payload.getOrDefault("name", "Demo User"),
                "email", payload.getOrDefault("email", "demo@example.com")
        ));
    }

    @PatchMapping("/users/me/password")
    public Map<String, Object> updateCurrentUserPassword(@RequestBody Map<String, Object> payload) {
        return ApiResponse.ok("Password updated", Map.of(
                "changed", true,
                "requiresReauth", false,
                "newPasswordLength", payload.getOrDefault("newPassword", "").toString().length()
        ));
    }

    @GetMapping("/users")
    public Map<String, Object> listUsers(@RequestParam(required = false) String search) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("search", search);
        data.put("items", new Object[]{
                Map.of("id", 1, "name", "Demo User", "email", "demo@example.com")
        });
        return ApiResponse.ok("Users fetched", data);
    }

    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody Map<String, Object> payload) {
        return ApiResponse.created("User created", Map.of(
                "id", 101,
                "name", payload.getOrDefault("name", ""),
                "email", payload.getOrDefault("email", "")
        ));
    }

    @GetMapping("/users/{id}")
    public Map<String, Object> getUser(@PathVariable long id) {
        return ApiResponse.ok("User fetched", Map.of(
                "id", id,
                "name", "Demo User",
                "email", "demo@example.com"
        ));
    }

    @PatchMapping("/users/{id}")
    public Map<String, Object> updateUser(@PathVariable long id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.ok("User updated", Map.of(
                "id", id,
                "name", payload.getOrDefault("name", "Demo User"),
                "email", payload.getOrDefault("email", "demo@example.com")
        ));
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable long id) {
        return ApiResponse.ok("User deleted", Map.of("id", id));
    }
}
