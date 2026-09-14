package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    @GetMapping
    public Map<String, Object> listRoles() {
        return ApiResponse.ok("Roles fetched", new Object[]{
                Map.of("id", 1, "name", "admin"),
                Map.of("id", 2, "name", "agent")
        });
    }

    @PostMapping
    public Map<String, Object> createRole(@RequestBody Map<String, Object> payload) {
        return ApiResponse.created("Role created", Map.of(
                "id", 10,
                "name", payload.getOrDefault("name", "")
        ));
    }

    @GetMapping("/{id}")
    public Map<String, Object> getRole(@PathVariable long id) {
        return ApiResponse.ok("Role fetched", Map.of("id", id, "name", "admin"));
    }

    @PatchMapping("/{id}")
    public Map<String, Object> updateRole(@PathVariable long id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.ok("Role updated", Map.of(
                "id", id,
                "name", payload.getOrDefault("name", "admin")
        ));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteRole(@PathVariable long id) {
        return ApiResponse.ok("Role deleted", Map.of("id", id));
    }
}
