package com.devsu.fintech.banking_api.controller;

import com.devsu.fintech.banking_api.dto.ReportTransactionsDTO;
import com.devsu.fintech.banking_api.excepcion.GlobalExceptionHandler;
import com.devsu.fintech.banking_api.excepcion.ResourceNotFoundException;
import com.devsu.fintech.banking_api.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController controller;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(converter)
                .build();
    }

    private ReportTransactionsDTO.StatusAcountResponse sampleReport() {
        var resume = new ReportTransactionsDTO.Resume(
                "John Doe",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                new BigDecimal("500.00"),
                new BigDecimal("200.00"),
                List.of()
        );
        return new ReportTransactionsDTO.StatusAcountResponse(resume, "JVBERi0xLjQ=");
    }

    @Test
    void generate_returnsReportJson() throws Exception {
        when(reportService.generate(eq("john01"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(sampleReport());

        mockMvc.perform(get("/reportes")
                        .param("clientId", "john01")
                        .param("from", "2026-01-01")
                        .param("to", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.report.client").value("John Doe"))
                .andExpect(jsonPath("$.report.totalCredits").value(500.00))
                .andExpect(jsonPath("$.report.totalDebits").value(200.00))
                .andExpect(jsonPath("$.pdfBase64").value("JVBERi0xLjQ="));
    }

    @Test
    void generate_returnsEmptyTransactionsList() throws Exception {
        when(reportService.generate(eq("john01"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(sampleReport());

        mockMvc.perform(get("/reportes")
                        .param("clientId", "john01")
                        .param("from", "2026-01-01")
                        .param("to", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.report.transactions").isEmpty());
    }



    @Test
    void generate_returnsNotFoundWhenClientMissing() throws Exception {
        when(reportService.generate(eq("unknown"), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new ResourceNotFoundException("Client not found"));

        mockMvc.perform(get("/reportes")
                        .param("clientId", "unknown")
                        .param("from", "2026-01-01")
                        .param("to", "2026-01-31"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }


    @Test
    void downloadPdf_returnsNotFoundWhenClientMissing() throws Exception {
        when(reportService.generatePdf(eq("unknown"), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new ResourceNotFoundException("Client not found"));

        mockMvc.perform(get("/reportes/pdf")
                        .param("clientId", "unknown")
                        .param("from", "2026-01-01")
                        .param("to", "2026-01-31"))
                .andExpect(status().isNotFound());
    }
}
