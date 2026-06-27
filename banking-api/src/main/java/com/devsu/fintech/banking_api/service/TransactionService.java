package com.devsu.fintech.banking_api.service;

import com.devsu.fintech.banking_api.dto.TransactionRequestDTO;
import com.devsu.fintech.banking_api.dto.TransactionResponseDTO;

import java.util.List;

public interface TransactionService {
    TransactionResponseDTO register(TransactionRequestDTO dto);
    List<TransactionResponseDTO> list();
    List<TransactionResponseDTO> listByAccount(String accountNumber);
    TransactionResponseDTO findById(Long id);
    void delete(Long id);
}
