package com.devsu.fintech.banking_api.mapper;

import com.devsu.fintech.banking_api.dto.AccountResponseDTO;
import com.devsu.fintech.banking_api.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "client.clientId", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    AccountResponseDTO toResponse(Account account);
}
