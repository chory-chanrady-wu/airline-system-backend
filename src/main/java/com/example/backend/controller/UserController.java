package com.example.backend.controller;

import com.example.backend.dto.request.UserCreateRequest;
import com.example.backend.dto.request.UserPasswordUpdateRequest;
import com.example.backend.dto.request.UserUpdateRequest;
import com.example.backend.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/me")
    public Map<String, Object> getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PatchMapping("/users/me")
    public Map<String, Object> updateCurrentUser(@RequestBody UserUpdateRequest request) {
        return userService.updateCurrentUser(request);
    }

    @PatchMapping("/users/me/password")
    public Map<String, Object> updateCurrentUserPassword(@RequestBody UserPasswordUpdateRequest request) {
        return userService.updateCurrentUserPassword(request);
    }

    @GetMapping("/users")
    public Map<String, Object> listUsers(@RequestParam(required = false) String search) {
        return userService.listUsers(search);
    }

    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/users/{id}")
    public Map<String, Object> getUser(@PathVariable Integer id) {
        return userService.getUser(id);
    }

    @PatchMapping("/users/{id}")
    public Map<String, Object> updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }
}