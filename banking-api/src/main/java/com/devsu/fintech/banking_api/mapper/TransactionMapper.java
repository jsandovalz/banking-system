package com.devsu.fintech.banking_api.mapper;

import com.devsu.fintech.banking_api.dto.TransactionResponseDTO;
import com.devsu.fintech.banking_api.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "account.accountNumber", target = "accountNumber")
    TransactionResponseDTO toResponse(Transaction transaction);
}
