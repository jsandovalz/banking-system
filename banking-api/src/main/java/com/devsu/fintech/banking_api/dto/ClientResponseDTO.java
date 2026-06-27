package com.devsu.fintech.banking_api.dto;

public record ClientResponseDTO(
        Long id,
        String name,
        String gender,
        Integer age,
        String identification,
        String address,
        String phone,
        String clientId,
        Boolean status
) {}