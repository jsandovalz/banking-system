package com.devsu.fintech.banking_api.service;

import com.devsu.fintech.banking_api.dto.ReportTransactionsDTO;

import java.time.LocalDate;

public interface ReportService {
    ReportTransactionsDTO.StatusAcountResponse generate(String clientId, LocalDate from, LocalDate to);
    byte[] generatePdf(String clientId, LocalDate from, LocalDate to);
}
