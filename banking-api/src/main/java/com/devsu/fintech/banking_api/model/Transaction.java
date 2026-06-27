package com.devsu.fintech.banking_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;
    @NotNull
    @Column(nullable = false)
    private LocalDateTime date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_types", nullable = false,length = 20)
    private TransactionType transactionType;

    @NotNull
    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "account_id",nullable = false)
    private Account account;

    @PrePersist
    void prePersist() {
        if (date == null) {
            date = LocalDateTime.now();
        }
    }

}

