package com.example.backend.controller;

import com.example.backend.entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "spring.sql.init.enabled=false",
        "spring.docker.compose.enabled=false"
})
class RoleControllerSerializationTest {

    @Autowired
    private RoleController roleController;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void listRolesSerializesPermissionsWithoutLazyInitializationError() throws Exception {
        String roleName = "role_" + UUID.randomUUID().toString().replace('-', '_');

        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            Role role = Role.builder()
                    .name(roleName)
                    .description("Temporary test role")
                    .permissions(new ArrayList<>(List.of("ROLE_READ", "ROLE_WRITE")))
                    .build();
            entityManager.persist(role);
            entityManager.flush();
        });

        Map<String, Object> response = roleController.listRoles();
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        List<?> items = (List<?>) data.get("items");
        Map<String, Object> createdRole = null;
        for (Object item : items) {
            Map<String, Object> candidate = (Map<String, Object>) item;
            if (roleName.equals(candidate.get("name"))) {
                createdRole = candidate;
                break;
            }
        }

        assertTrue(createdRole != null, "Created role should be present in the response items");
        Object permissions = createdRole.get("permissions");
        assertTrue(permissions instanceof List, "Permissions should be returned as a plain List");
        assertTrue(permissions.getClass().getName().contains("ArrayList"),
                "Permissions should be materialized into a concrete list before leaving the transaction");
    }
}


