package com.devsu.fintech.banking_api.service;

import com.devsu.fintech.banking_api.config.BankingProperties;
import com.devsu.fintech.banking_api.dto.TransactionRequestDTO;
import com.devsu.fintech.banking_api.excepcion.BalanceNotFoundException;
import com.devsu.fintech.banking_api.excepcion.DailyLimitExceededException;
import com.devsu.fintech.banking_api.mapper.TransactionMapper;
import com.devsu.fintech.banking_api.model.*;
import com.devsu.fintech.banking_api.repository.AccountRepository;
import com.devsu.fintech.banking_api.repository.TransactionRepository;
import com.devsu.fintech.banking_api.service.impl.TransactionServiceImpl;
import com.devsu.fintech.banking_api.transaction.strategy.DebitStrategy;
import com.devsu.fintech.banking_api.transaction.strategy.DepositStrategy;
import com.devsu.fintech.banking_api.transaction.strategy.TransactionStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionServiceImpl")
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    private TransactionServiceImpl service;
    private BankingProperties props;

    @BeforeEach
    void setup() {
        props = new BankingProperties(new java.math.BigDecimal("1000.00"));
        DepositStrategy deposit = new DepositStrategy();
        DebitStrategy debit = new DebitStrategy(props, transactionRepository);
        TransactionStrategyFactory factory = new TransactionStrategyFactory(List.of(deposit, debit));

        service = new TransactionServiceImpl(transactionRepository, accountRepository, transactionMapper, factory);
    }

    private Account accountWithBalance(BigDecimal balance) {
        Client client = Client.builder()
                .clientId("C1")
                .name("Test Client")
                .password("xxxx")
                .status(true)
                .gender("M")
                .age(30)
                .identification("1234567890")
                .address("Test Address")
                .phone("123456789")
                .build();
        return Account.builder()
                .id(1L)
                .accountNumber("478758")
                .accountType(AccountType.AHORRO)
                .initialBalance(balance)
                .availableBalance(balance)
                .status(true)
                .client(client)
                .build();
    }


    @Test
    void must_throw_when_balance_exceeds_available_balance_when_balance_zero() {
        Account account = accountWithBalance(BigDecimal.ZERO);
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));

        TransactionRequestDTO requestDTO = new TransactionRequestDTO("478758", TransactionType.RETIRO,
                new BigDecimal("100.00"));

        assertThatThrownBy(() -> service.register(requestDTO))
                .isInstanceOf(BalanceNotFoundException.class)
                .hasMessageContaining("Balance not found for account: "+account.getId());
    }

    @Test
    void must_throw_when_withdraw_exceeds_daily_limit() {

        Account account = accountWithBalance(new BigDecimal("5000.00"));
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(transactionRepository.sumDailyTransactions(eq("478758"), eq(TransactionType.RETIRO),
                any(LocalDateTime.class),any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("950.00"));

        TransactionRequestDTO requestDTO = new TransactionRequestDTO("478758", TransactionType.RETIRO,
                new BigDecimal("100.00"));

        assertThatThrownBy(() -> service.register(requestDTO))
                .isInstanceOf(DailyLimitExceededException.class)
                .hasMessageContaining("Daily transaction limit exceeded");

    }
    @Test
    void deposit_should_add_to_balance() {
        Account account = accountWithBalance(new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber("225487")).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));


        TransactionRequestDTO requestDTO = new TransactionRequestDTO("225487", TransactionType.DEPOSITO,
                new BigDecimal("600"));

        service.register(requestDTO);

        assertThat(account.getAvailableBalance()).isEqualByComparingTo("700.00");
    }

    @Test
    void withdraw_should_subtract_from_balance() {
        Account account = accountWithBalance(new BigDecimal("2000.00"));
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(transactionRepository.sumDailyTransactions(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionRequestDTO requestDTO = new TransactionRequestDTO("478758", TransactionType.RETIRO,
                new BigDecimal("575"));

        service.register(requestDTO);

        assertThat(account.getAvailableBalance()).isEqualByComparingTo("1425.00");
    }

}

