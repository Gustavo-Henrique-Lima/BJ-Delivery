package com.bj_food.ms_usuario.dto.response;

import com.bj_food.ms_usuario.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String keycloakId,
        String name,
        String email,
        String phone,
        Boolean active,
        LocalDateTime createdAt,
        List<AddressResponse> addresses
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getKeycloakId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getActive(),
                user.getCreatedAt(),
                user.getAddresses().stream().map(AddressResponse::from).toList()
        );
    }
}