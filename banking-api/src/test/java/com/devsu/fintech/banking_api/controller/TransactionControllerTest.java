package com.devsu.fintech.banking_api.controller;

import com.devsu.fintech.banking_api.dto.TransactionRequestDTO;
import com.devsu.fintech.banking_api.dto.TransactionResponseDTO;
import com.devsu.fintech.banking_api.excepcion.GlobalExceptionHandler;
import com.devsu.fintech.banking_api.excepcion.ResourceNotFoundException;
import com.devsu.fintech.banking_api.model.TransactionType;
import com.devsu.fintech.banking_api.service.TransactionService;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController controller;

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

    private TransactionResponseDTO sampleResponse() {
        return new TransactionResponseDTO(
                1L,
                LocalDateTime.of(2026, 1, 15, 10, 0),
                TransactionType.DEPOSITO,
                new BigDecimal("200.00"),
                new BigDecimal("1200.00"),
                "ACC-001"
        );
    }

    private TransactionRequestDTO sampleRequest() {
        return new TransactionRequestDTO("ACC-001", TransactionType.DEPOSITO, new BigDecimal("200.00"));
    }

    @Test
    void list_returnsAllTransactions() throws Exception {
        when(transactionService.list()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-001"))
                .andExpect(jsonPath("$[0].transactionType").value("DEPOSITO"));
    }

    @Test
    void list_returnsEmptyList() throws Exception {
        when(transactionService.list()).thenReturn(List.of());

        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void list_filtersByAccountNumber() throws Exception {
        when(transactionService.listByAccount("ACC-001")).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/movimientos").param("accountNumber", "ACC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-001"));
    }

    @Test
    void obtain_returnsTransaction() throws Exception {
        when(transactionService.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/movimientos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(200.00))
                .andExpect(jsonPath("$.balance").value(1200.00));
    }

    @Test
    void obtain_returnsNotFoundWhenMissing() throws Exception {
        when(transactionService.findById(99L)).thenThrow(new ResourceNotFoundException("Transaction not found"));

        mockMvc.perform(get("/movimientos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Transaction not found"));
    }

    @Test
    void register_returnsCreatedDeposit() throws Exception {
        when(transactionService.register(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionType").value("DEPOSITO"))
                .andExpect(jsonPath("$.amount").value(200.00));
    }

    @Test
    void register_returnsCreatedDebit() throws Exception {
        TransactionRequestDTO debit = new TransactionRequestDTO("ACC-001", TransactionType.RETIRO,
                new BigDecimal("100.00"));
        TransactionResponseDTO debitResponse = new TransactionResponseDTO(2L, LocalDateTime.now(),
                TransactionType.RETIRO, new BigDecimal("100.00"), new BigDecimal("900.00"), "ACC-001");
        when(transactionService.register(any())).thenReturn(debitResponse);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionType").value("RETIRO"))
                .andExpect(jsonPath("$.balance").value(900.00));
    }

    @Test
    void register_returnsBadRequestWhenAccountNumberIsBlank() throws Exception {
        TransactionRequestDTO invalid = new TransactionRequestDTO("", TransactionType.DEPOSITO,
                new BigDecimal("200.00"));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void register_returnsBadRequestWhenAmountIsZero() throws Exception {
        TransactionRequestDTO invalid = new TransactionRequestDTO("ACC-001", TransactionType.DEPOSITO,
                BigDecimal.ZERO);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void register_returnsBadRequestWhenAmountIsNegative() throws Exception {
        TransactionRequestDTO invalid = new TransactionRequestDTO("ACC-001", TransactionType.DEPOSITO,
                new BigDecimal("-50.00"));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        doNothing().when(transactionService).delete(1L);

        mockMvc.perform(delete("/movimientos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returnsNotFoundWhenMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Transaction not found")).when(transactionService).delete(99L);

        mockMvc.perform(delete("/movimientos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }
}
