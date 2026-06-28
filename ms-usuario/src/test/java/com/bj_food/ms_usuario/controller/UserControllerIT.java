package com.bj_food.ms_usuario.controller;

import com.bj_food.ms_usuario.domain.model.User;
import com.bj_food.ms_usuario.dto.request.CreateUserRequest;
import com.bj_food.ms_usuario.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;
    private String keycloakId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        keycloakId = "keycloak-test-" + UUID.randomUUID();

        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setName("John Doe");
        user.setEmail("john-" + UUID.randomUUID() + "@email.com");
        user.setPhone("81999990001");
        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("should find user by id when owner")
    void findById_success_owner() throws Exception {
        mockMvc.perform(get("/api/usuarios/{id}", savedUser.getId())
                        .with(jwt().jwt(j -> j.subject(keycloakId))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value(keycloakId));
    }

    @Test
    @DisplayName("should find user by id when admin")
    void findById_success_admin() throws Exception {
        mockMvc.perform(get("/api/usuarios/{id}", savedUser.getId())
                        .with(jwt().jwt(j -> j.subject("other-keycloak-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value(keycloakId));
    }

    @Test
    @DisplayName("should return 401 when no token")
    void findById_unauthorized() throws Exception {
        mockMvc.perform(get("/api/usuarios/{id}", savedUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should return 403 when accessing another user")
    void findById_forbidden() throws Exception {
        mockMvc.perform(get("/api/usuarios/{id}", savedUser.getId())
                        .with(jwt().jwt(j -> j.subject("other-keycloak-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 404 when user not found")
    void findById_notFound() throws Exception {
        mockMvc.perform(get("/api/usuarios/{id}", UUID.randomUUID())
                        .with(jwt().jwt(j -> j.subject(keycloakId))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should update user successfully")
    void updateUser_success() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "John Updated", savedUser.getEmail(), "81999990002", keycloakId
        );

        mockMvc.perform(put("/api/usuarios/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().jwt(j -> j.subject(keycloakId))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));
    }

    @Test
    @DisplayName("should return 403 when updating another user")
    void updateUser_forbidden() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "John Updated", savedUser.getEmail(), "81999990002", keycloakId
        );

        mockMvc.perform(put("/api/usuarios/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().jwt(j -> j.subject("other-keycloak-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should deactivate user successfully")
    void deactivateUser_success() throws Exception {
        mockMvc.perform(delete("/api/usuarios/{id}", savedUser.getId())
                        .with(jwt().jwt(j -> j.subject(keycloakId))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return 403 when deactivating another user")
    void deactivateUser_forbidden() throws Exception {
        mockMvc.perform(delete("/api/usuarios/{id}", savedUser.getId())
                        .with(jwt().jwt(j -> j.subject("other-keycloak-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isForbidden());
    }
}