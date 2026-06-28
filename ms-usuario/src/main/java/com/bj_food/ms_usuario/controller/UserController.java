package com.bj_food.ms_usuario.controller;

import com.bj_food.ms_usuario.dto.request.CreateAddressRequest;
import com.bj_food.ms_usuario.dto.request.CreateUserRequest;
import com.bj_food.ms_usuario.dto.response.AddressResponse;
import com.bj_food.ms_usuario.dto.response.UserResponse;
import com.bj_food.ms_usuario.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.create(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable UUID id,
            Authentication authentication) {
        return ResponseEntity.ok(userService.findById(id, authentication));
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserResponse> findByKeycloakId(
            @PathVariable String keycloakId,
            Authentication authentication) {
        return ResponseEntity.ok(userService.findByKeycloakId(keycloakId, authentication));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(userService.update(id, request, authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id,
            Authentication authentication) {
        userService.deactivate(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressResponse> addAddress(
            @PathVariable UUID id,
            @Valid @RequestBody CreateAddressRequest request,
            Authentication authentication) {
        AddressResponse response = userService.addAddress(id, request, authentication);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{addressId}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<AddressResponse>> findAddresses(
            @PathVariable UUID id,
            Authentication authentication) {
        return ResponseEntity.ok(userService.findAddresses(id, authentication));
    }
}