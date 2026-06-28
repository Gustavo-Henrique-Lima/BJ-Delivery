package com.bj_food.ms_usuario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(

        @NotBlank(message = "O campo rua é obrigatório")
        String street,

        @NotBlank(message = "O campo número é obrigatório")
        String number,

        String complement,

        @NotBlank(message = "O campo bairro é obrigatório")
        String neighborhood,

        @NotBlank(message = "O campo cidade é obrigatório")
        String city,

        @NotBlank(message = "O campo estado é obrigatório")
        @Size(min = 2, max = 2, message = "O campo estado deve ter 2 caracteres")
        String state,

        @NotBlank(message = "O campo CEP é obrigatório")
        @Pattern(regexp = "\\d{5}-\\d{3}", message = "O campo CEP deve ser informado no formato 00000-000")
        String zipCode,

        boolean main
) {
}