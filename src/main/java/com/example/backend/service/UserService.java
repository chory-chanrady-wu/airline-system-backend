package com.example.backend.service;

import com.example.backend.dto.request.UserCreateRequest;
import com.example.backend.dto.request.UserPasswordUpdateRequest;
import com.example.backend.dto.request.UserUpdateRequest;

import java.util.Map;

public interface UserService {
    Map<String, Object> getCurrentUser();

    Map<String, Object> updateCurrentUser(UserUpdateRequest request);

    Map<String, Object> updateCurrentUserPassword(UserPasswordUpdateRequest request);

    Map<String, Object> listUsers(String search);

    Map<String, Object> createUser(UserCreateRequest request);

    Map<String, Object> getUser(Integer id);

    Map<String, Object> updateUser(Integer id, UserUpdateRequest request);

    Map<String, Object> deleteUser(Integer id);
}

