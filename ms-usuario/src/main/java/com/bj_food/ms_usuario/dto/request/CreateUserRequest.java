package com.bj_food.ms_usuario.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "O campo nome é obrigatório")
        @Size(max = 100, message = "O campo nome deve ter menos de 100 caracteres")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @Size(max = 12, message = "O campo número deve ter 12 caracteres")
        String phone,

        @NotBlank(message = "Keycloak ID é obrigatório")
        String keycloakId
) {
}