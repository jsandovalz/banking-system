package com.devsu.fintech.banking_api.dto;

import jakarta.validation.constraints.*;

public record ClientRequestDTO(
        @NotBlank(message = "Name is required") @Size(max = 100)
        String name,

        @NotBlank @Pattern(regexp = "M|F|O", message = "Gender must be M, F, or O")
        String gender,

        @NotNull @Min(0) @Max(150)
        Integer age,

        @NotBlank @Size(max = 20)
        String identification,

        @NotBlank @Size(max = 200)
        String address,

        @NotBlank @Size(max = 20)
        String phone,

        @NotBlank @Size(max = 30)
        String clientId,

        @NotBlank @Size(min = 4, max = 100)
        String password,

        @NotNull
        Boolean status
) {}

