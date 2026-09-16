package com.example.backend.service.impl;

import com.example.backend.controller.ApiResponse;
import com.example.backend.dto.request.RoleCreateRequest;
import com.example.backend.dto.request.RoleUpdateRequest;
import com.example.backend.entity.Role;
import com.example.backend.service.RoleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> listRoles() {
        List<Role> roles = entityManager.createQuery("select r from Role r order by r.id asc", Role.class)
                .getResultList();
        List<Map<String, Object>> items = new ArrayList<>();
        for (Role role : roles) {
            items.add(roleData(role));
        }
        return ApiResponse.ok("Roles fetched", Map.of("count", items.size(), "items", items));
    }

    @Override
    public Map<String, Object> createRole(RoleCreateRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            return ApiResponse.badRequest("Role name is required");
        }
        if (roleExists(request.name())) {
            return ApiResponse.badRequest("Role already exists");
        }
        Role role = Role.builder()
                .name(request.name())
                .description(request.description())
                .permissions(request.permissions() == null ? List.of() : new ArrayList<>(request.permissions()))
                .build();
        entityManager.persist(role);
        entityManager.flush();
        return ApiResponse.created("Role created", Map.of("role", roleData(role)));
    }

    @Override
    public Map<String, Object> getRole(Integer id) {
        Role role = findRole(id);
        if (role == null) {
            return ApiResponse.badRequest("Role not found");
        }
        return ApiResponse.ok("Role fetched", Map.of("role", roleData(role)));
    }

    @Override
    public Map<String, Object> updateRole(Integer id, RoleUpdateRequest request) {
        Role role = findRole(id);
        if (role == null) {
            return ApiResponse.badRequest("Role not found");
        }
        if (request.name() != null && !request.name().isBlank()) {
            role.setName(request.name());
        }
        if (request.description() != null && !request.description().isBlank()) {
            role.setDescription(request.description());
        }
        if (request.permissions() != null) {
            role.setPermissions(new ArrayList<>(request.permissions()));
        }
        entityManager.flush();
        return ApiResponse.ok("Role updated", Map.of("role", roleData(role)));
    }

    @Override
    public Map<String, Object> deleteRole(Integer id) {
        Role role = findRole(id);
        if (role == null) {
            return ApiResponse.badRequest("Role not found");
        }
        entityManager.remove(role);
        entityManager.flush();
        return ApiResponse.ok("Role deleted", Map.of("id", role.getId()));
    }

    private boolean roleExists(String name) {
        Long count = entityManager.createQuery("select count(r) from Role r where lower(r.name) = :name", Long.class)
                .setParameter("name", name.trim().toLowerCase(Locale.ROOT))
                .getSingleResult();
        return count != null && count > 0;
    }

    private Role findRole(Integer id) {
        return id == null ? null : entityManager.find(Role.class, id);
    }

    private Map<String, Object> roleData(Role role) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", role.getId());
        data.put("name", role.getName());
        data.put("description", role.getDescription());
        data.put("permissions", role.getPermissions() == null ? List.of() : new ArrayList<>(role.getPermissions()));
        data.put("createdAt", role.getCreatedAt());
        data.put("updatedAt", role.getUpdatedAt());
        return data;
    }
}

