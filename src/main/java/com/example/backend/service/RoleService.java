package com.example.backend.service;

import com.example.backend.dto.request.RoleCreateRequest;
import com.example.backend.dto.request.RoleUpdateRequest;

import java.util.Map;

public interface RoleService {
    Map<String, Object> listRoles();

    Map<String, Object> createRole(RoleCreateRequest request);

    Map<String, Object> getRole(Integer id);

    Map<String, Object> updateRole(Integer id, RoleUpdateRequest request);

    Map<String, Object> deleteRole(Integer id);
}

