package com.devsu.fintech.banking_api.service;

import com.devsu.fintech.banking_api.dto.AccountRequestDTO;
import com.devsu.fintech.banking_api.dto.AccountResponseDTO;

import java.util.List;

public interface AccountService {
    AccountResponseDTO create(AccountRequestDTO dto);
    AccountResponseDTO update(String accountNumber, AccountRequestDTO dto);
    AccountResponseDTO patchStatus(String accountNumber, Boolean status);
    void delete(String accountNumber);
    AccountResponseDTO find(String accountNumber);
    List<AccountResponseDTO> list();
    List<AccountResponseDTO> listByClient(String clientId);
}