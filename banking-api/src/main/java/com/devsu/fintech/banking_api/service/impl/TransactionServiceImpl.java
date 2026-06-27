package com.devsu.fintech.banking_api.service.impl;

import com.devsu.fintech.banking_api.dto.TransactionRequestDTO;
import com.devsu.fintech.banking_api.dto.TransactionResponseDTO;
import com.devsu.fintech.banking_api.excepcion.ResourceNotFoundException;
import com.devsu.fintech.banking_api.mapper.TransactionMapper;
import com.devsu.fintech.banking_api.model.Account;
import com.devsu.fintech.banking_api.model.Transaction;
import com.devsu.fintech.banking_api.model.TransactionType;
import com.devsu.fintech.banking_api.repository.AccountRepository;
import com.devsu.fintech.banking_api.repository.TransactionRepository;
import com.devsu.fintech.banking_api.service.TransactionService;
import com.devsu.fintech.banking_api.transaction.strategy.TransactionStrategy;
import com.devsu.fintech.banking_api.transaction.strategy.TransactionStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionStrategyFactory strategyFactory;

    @Override
    public TransactionResponseDTO register(TransactionRequestDTO dto) {
        Account account = accountRepository.findByAccountNumber(dto.accountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + dto.accountNumber()));

        //Strategy pattern: each type applies its own logic
        TransactionStrategy strategy = strategyFactory.of(dto.transactionType());
        BigDecimal newBalance = strategy.apply(account, dto.amount());

        // By convention: credit positive, debit negative in the amount field of the transaction
        BigDecimal registeredAmount = dto.transactionType() == TransactionType.RETIRO
                ? dto.amount().negate()
                : dto.amount();

        Transaction transaction = Transaction.builder()
                .date(LocalDateTime.now())
                .transactionType(dto.transactionType())
                .amount(registeredAmount)
                .balance(newBalance)
                .account(account)
                .build();
        accountRepository.save(account);
        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> list() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> listByAccount(String accountNumber) {
        return transactionRepository.findByAccountAccountNumberOrderByDateDesc(accountNumber).stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDTO findById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado: " + id));
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado: " + id));
        transactionRepository.delete(transaction);
    }
}
