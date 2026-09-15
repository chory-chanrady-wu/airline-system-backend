package com.example.backend.controller;

import com.example.backend.dto.request.UserCreateRequest;
import com.example.backend.dto.request.UserPasswordUpdateRequest;
import com.example.backend.dto.request.UserUpdateRequest;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.EntityLookupSupport;
import com.example.backend.service.PasswordHasher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Transactional
public class UserController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/users/me")
    public Map<String, Object> getCurrentUser() {
        User user = currentUser();
        if (user == null) {
            return ApiResponse.badRequest("No users available");
        }
        return ApiResponse.ok("Current user loaded", Map.of("user", userData(user)));
    }

    @PatchMapping("/users/me")
    public Map<String, Object> updateCurrentUser(@RequestBody UserUpdateRequest request) {
        User user = currentUser();
        if (user == null) {
            return ApiResponse.badRequest("No users available");
        }
        applyUserUpdate(user, request);
        entityManager.flush();
        return ApiResponse.ok("Profile updated", Map.of("user", userData(user)));
    }

    @PatchMapping("/users/me/password")
    public Map<String, Object> updateCurrentUserPassword(@RequestBody UserPasswordUpdateRequest request) {
        User user = currentUser();
        if (user == null) {
            return ApiResponse.badRequest("No users available");
        }
        if (!PasswordHasher.matches(EntityLookupSupport.safe(request.currentPassword()), user.getPasswordHash())) {
            return ApiResponse.badRequest("Current password is incorrect");
        }
        if (request.newPassword() == null || request.newPassword().isBlank()) {
            return ApiResponse.badRequest("New password is required");
        }
        user.setPasswordHash(PasswordHasher.sha256(request.newPassword()));
        entityManager.flush();
        return ApiResponse.ok("Password updated", Map.of(
                "changed", true,
                "requiresReauth", true,
                "user", userData(user)
        ));
    }

    @GetMapping("/users")
    public Map<String, Object> listUsers(@RequestParam(required = false) String search) {
        List<User> users = entityManager.createQuery("select u from User u order by u.id asc", User.class)
                .getResultList();
        String needle = EntityLookupSupport.normalized(search);
        List<Map<String, Object>> items = new ArrayList<>();
        for (User user : users) {
            if (!needle.isBlank()) {
                String roleName = user.getRole() == null ? "" : EntityLookupSupport.safe(user.getRole().getName());
                String haystack = (EntityLookupSupport.safe(user.getName()) + " "
                        + EntityLookupSupport.safe(user.getEmail()) + " " + roleName).toLowerCase(Locale.ROOT);
                if (!haystack.contains(needle)) {
                    continue;
                }
            }
            items.add(userData(user));
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("search", search);
        data.put("count", items.size());
        data.put("items", items);
        return ApiResponse.ok("Users fetched", data);
    }

    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody UserCreateRequest request) {
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
        Role role = resolveRole(request.roleId());
        if (role == null) {
            role = defaultRole();
        }
        if (role == null) {
            return ApiResponse.badRequest("A role is required before creating users");
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(PasswordHasher.sha256(request.password()))
                .role(role)
                .userType(User.UserType.SYSTEM_USER)
                .status(EntityLookupSupport.parseEnum(User.UserStatus.class, request.status(), User.UserStatus.Active))
                .build();
        entityManager.persist(user);
        entityManager.flush();
        return ApiResponse.created("User created", Map.of("user", userData(user)));
    }

    @GetMapping("/users/{id}")
    public Map<String, Object> getUser(@PathVariable Integer id) {
        User user = findUser(id);
        if (user == null) {
            return ApiResponse.badRequest("User not found");
        }
        return ApiResponse.ok("User fetched", Map.of("user", userData(user)));
    }

    @PatchMapping("/users/{id}")
    public Map<String, Object> updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest request) {
        User user = findUser(id);
        if (user == null) {
            return ApiResponse.badRequest("User not found");
        }
        applyUserUpdate(user, request);
        entityManager.flush();
        return ApiResponse.ok("User updated", Map.of("user", userData(user)));
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable Integer id) {
        User user = findUser(id);
        if (user == null) {
            return ApiResponse.badRequest("User not found");
        }
        entityManager.remove(user);
        entityManager.flush();
        return ApiResponse.ok("User deleted", Map.of("id", id));
    }

    private void applyUserUpdate(User user, UserUpdateRequest request) {
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }
        Role role = resolveRole(request.roleId());
        if (role != null) {
            user.setRole(role);
        }
        if (request.status() != null && !request.status().isBlank()) {
            user.setStatus(EntityLookupSupport.parseEnum(User.UserStatus.class, request.status(), user.getStatus()));
        }
    }

    private User currentUser() {
        List<User> users = entityManager.createQuery("select u from User u order by u.updatedAt desc, u.id desc", User.class)
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    private User findUser(Integer id) {
        return id == null ? null : entityManager.find(User.class, id);
    }

    private boolean emailExists(String email) {
        Long count = entityManager.createQuery("select count(u) from User u where lower(u.email) = :email", Long.class)
                .setParameter("email", email.toLowerCase(Locale.ROOT))
                .getSingleResult();
        return count != null && count > 0;
    }

    private Role resolveRole(String roleIdentifier) {
        Integer roleId = EntityLookupSupport.parseInteger(roleIdentifier);
        if (roleId != null) {
            return entityManager.find(Role.class, roleId);
        }
        if (roleIdentifier == null || roleIdentifier.isBlank()) {
            return null;
        }
        List<Role> roles = entityManager.createQuery("select r from Role r where lower(r.name) = :name", Role.class)
                .setParameter("name", roleIdentifier.trim().toLowerCase(Locale.ROOT))
                .getResultList();
        return roles.isEmpty() ? null : roles.get(0);
    }

    private Role defaultRole() {
        List<Role> preferred = entityManager.createQuery("select r from Role r where lower(r.name) = :name", Role.class)
                .setParameter("name", "passenger")
                .getResultList();
        if (!preferred.isEmpty()) {
            return preferred.get(0);
        }
        List<Role> roles = entityManager.createQuery("select r from Role r order by r.id asc", Role.class)
                .getResultList();
        return roles.isEmpty() ? null : roles.get(0);
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
}