package com.devsu.fintech.banking_api.mapper;

import com.devsu.fintech.banking_api.dto.ClientRequestDTO;
import com.devsu.fintech.banking_api.dto.ClientResponseDTO;
import com.devsu.fintech.banking_api.model.Client;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    Client toEntity(ClientRequestDTO dto);

    ClientResponseDTO toResponse(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    void updateEntity(ClientRequestDTO dto, @MappingTarget Client client);
}

