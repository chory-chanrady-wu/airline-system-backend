package com.example.backend.controller;

import com.example.backend.dto.request.RoleCreateRequest;
import com.example.backend.dto.request.RoleUpdateRequest;
import com.example.backend.entity.Role;
import com.example.backend.service.EntityLookupSupport;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
@Transactional
public class RoleController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public Map<String, Object> listRoles() {
        List<Role> roles = entityManager.createQuery("select r from Role r order by r.id asc", Role.class)
                .getResultList();
        List<Map<String, Object>> items = new ArrayList<>();
        for (Role role : roles) {
            items.add(roleData(role));
        }
        return ApiResponse.ok("Roles fetched", Map.of("count", items.size(), "items", items));
    }

    @PostMapping
    public Map<String, Object> createRole(@RequestBody RoleCreateRequest request) {
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

    @GetMapping("/{id}")
    public Map<String, Object> getRole(@PathVariable Integer id) {
        Role role = findRole(id);
        if (role == null) {
            return ApiResponse.badRequest("Role not found");
        }
        return ApiResponse.ok("Role fetched", Map.of("role", roleData(role)));
    }

    @PatchMapping("/{id}")
    public Map<String, Object> updateRole(@PathVariable Integer id, @RequestBody RoleUpdateRequest request) {
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

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteRole(@PathVariable Integer id) {
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
