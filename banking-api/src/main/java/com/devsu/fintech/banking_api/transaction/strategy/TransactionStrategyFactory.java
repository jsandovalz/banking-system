package com.devsu.fintech.banking_api.transaction.strategy;

import com.devsu.fintech.banking_api.model.TransactionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TransactionStrategyFactory {

    private final Map<TransactionType,TransactionStrategy> strategies;

    public TransactionStrategyFactory(List<TransactionStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(TransactionStrategy::getType, Function.identity()));
    }

    public TransactionStrategy of(TransactionType type) {
        TransactionStrategy transactionStrategy = strategies.get(type);
        if(transactionStrategy == null) {
            throw new IllegalArgumentException("Transactio type not support: " + type);
        }
        return transactionStrategy;
    }
}

