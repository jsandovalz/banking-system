package com.devsu.fintech.banking_api.service.impl;

import com.devsu.fintech.banking_api.dto.AccountRequestDTO;
import com.devsu.fintech.banking_api.dto.AccountResponseDTO;
import com.devsu.fintech.banking_api.excepcion.DuplicateResourceException;
import com.devsu.fintech.banking_api.excepcion.ResourceNotFoundException;
import com.devsu.fintech.banking_api.mapper.AccountMapper;
import com.devsu.fintech.banking_api.model.Account;
import com.devsu.fintech.banking_api.model.Client;
import com.devsu.fintech.banking_api.repository.AccountRepository;
import com.devsu.fintech.banking_api.repository.ClientRepository;
import com.devsu.fintech.banking_api.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final AccountMapper accountMapper;


    @Override
    public AccountResponseDTO create(AccountRequestDTO dto) {
        if(accountRepository.existsByAccountNumber(dto.accountNumber())) {
            throw new DuplicateResourceException("The Account already exists " + dto.accountNumber());
        }
        Client client = clientRepository.findByClientId(dto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + dto.clientId()));

        Account account = Account.builder()
                .accountNumber(dto.accountNumber())
                .accountType(dto.accountType())
                .initialBalance(dto.initialBalance())
                .availableBalance(dto.initialBalance())
                .status(dto.status())
                .client(client)
                .build();
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponseDTO update(String accountNumber, AccountRequestDTO dto) {
        Account account = getAccount(accountNumber);
        account.setAccountType(dto.accountType());
        if(account.getTransactions().isEmpty()) {
            account.setInitialBalance(dto.initialBalance());
            account.setAvailableBalance(dto.initialBalance());
        }
        account.setStatus(dto.status());
        if(!account.getClient().getClientId().equals(dto.clientId())) {
            Client client = clientRepository.findByClientId(dto.clientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + dto.clientId()));
            account.setClient(client);
        }

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponseDTO patchStatus(String accountNumber, Boolean status) {
        Account account = getAccount(accountNumber);
        account.setStatus(status);
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public void delete(String accountNumber) {
        Account account = getAccount(accountNumber);
        accountRepository.delete(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO find(String accountNumber) {
        return accountMapper.toResponse(getAccount(accountNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> list() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> listByClient(String clientId) {
        return accountRepository.findByClientClientId(clientId).stream()
                .map(accountMapper::toResponse)
                .toList();
    }
    private Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
    }
}