package com.devsu.fintech.banking_api.controller;

import com.devsu.fintech.banking_api.dto.ReportTransactionsDTO;
import com.devsu.fintech.banking_api.service.ReportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Estado de cuenta")
public class ReportController {

    private final ReportService reportService;

    /**
     * GET /reportes?clienteId=...&desde=YYYY-MM-DD&hasta=YYYY-MM-DD
     * @param clientId
     * @param from
     * @param to
     * @return JSON + PDF in base64
     */
    @GetMapping
    public ReportTransactionsDTO.StatusAcountResponse generate(
            @RequestParam @Parameter(description = "Identificador del cliente") String clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate to) {
        return reportService.generate(clientId,from,to);

    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam String clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        byte[] pdf = reportService.generatePdf(clientId,from,to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=estado-cuenta.pdf")
                .body(pdf);
    }

}
