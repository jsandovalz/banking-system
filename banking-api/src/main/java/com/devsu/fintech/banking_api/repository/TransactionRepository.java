package com.devsu.fintech.banking_api.repository;

import com.devsu.fintech.banking_api.model.Transaction;
import com.devsu.fintech.banking_api.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountAccountNumberOrderByDateDesc(String accountNumber);

    /**
     * Total withdrawals for the day for a given account.
     * Used to validate the daily withdrawal limit.
     * @param accountNumber
     * @param type
     * @param startOfDay
     * @param endOfDay
     * @return
     */
    @Query("""
            SELECT COALESCE(ABS(SUM(t.amount)), 0)
              FROM Transaction t
             WHERE t.account.accountNumber = :accountNumber
               AND t.transactionType = :type
               AND t.date >= :initDay
               AND t.date <  :endDay
            """)
    BigDecimal sumDailyTransactions(@Param("accountNumber") String accountNumber,
                                    @Param("type") TransactionType type,
                                    @Param("initDay") LocalDateTime startOfDay,
                                    @Param("endDay") LocalDateTime endOfDay);

    /**
     * Transactions for report: by client and date range.
     * @param clientId
     * @param from
     * @param to
     * @return
     */
    /**
     * Half-open interval [from, to): inclusive en :from, exclusivo en :to.
     * Combina con LocalDate#atStartOfDay() y to.plusDays(1).atStartOfDay()
     * para evitar duplicar transacciones en rangos contiguos.
     */
    @Query("""
            SELECT t
              FROM Transaction t
              JOIN t.account a
              JOIN a.client c
             WHERE c.clientId = :clientId
               AND t.date >= :from
               AND t.date <  :to
             ORDER BY t.date ASC
            """)
    List<Transaction> reportAccountStatement(@Param("clientId") String clientId,
                                             @Param("from") LocalDateTime from,
                                             @Param("to") LocalDateTime to);

}
