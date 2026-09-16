package com.example.backend.controller;

import com.example.backend.dto.request.RoleCreateRequest;
import com.example.backend.dto.request.RoleUpdateRequest;
import com.example.backend.service.RoleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public Map<String, Object> listRoles() {
        return roleService.listRoles();
    }

    @PostMapping
    public Map<String, Object> createRole(@RequestBody RoleCreateRequest request) {
        return roleService.createRole(request);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getRole(@PathVariable Integer id) {
        return roleService.getRole(id);
    }

    @PatchMapping("/{id}")
    public Map<String, Object> updateRole(@PathVariable Integer id, @RequestBody RoleUpdateRequest request) {
        return roleService.updateRole(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteRole(@PathVariable Integer id) {
        return roleService.deleteRole(id);
    }
}
