package com.devsu.fintech.banking_api.controller;

import com.devsu.fintech.banking_api.dto.AccountRequestDTO;
import com.devsu.fintech.banking_api.dto.AccountResponseDTO;
import com.devsu.fintech.banking_api.excepcion.DuplicateResourceException;
import com.devsu.fintech.banking_api.excepcion.GlobalExceptionHandler;
import com.devsu.fintech.banking_api.excepcion.ResourceNotFoundException;
import com.devsu.fintech.banking_api.model.AccountType;
import com.devsu.fintech.banking_api.service.AccountService;
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
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController controller;

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

    private AccountResponseDTO sampleResponse() {
        return new AccountResponseDTO(1L, "ACC-001", AccountType.AHORRO, new BigDecimal("1000.00"),
                new BigDecimal("900.00"), true, "john01", "John Doe");
    }

    private AccountRequestDTO sampleRequest() {
        return new AccountRequestDTO("ACC-001", AccountType.AHORRO, new BigDecimal("1000.00"),
                true, "john01");
    }

    @Test
    void list_returnsAllAccounts() throws Exception {
        when(accountService.list()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-001"))
                .andExpect(jsonPath("$[0].accountType").value("AHORRO"));
    }

    @Test
    void list_returnsEmptyList() throws Exception {
        when(accountService.list()).thenReturn(List.of());

        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void list_filtersByClientId() throws Exception {
        when(accountService.listByClient("john01")).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/cuentas").param("cliendId", "john01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientId").value("john01"));
    }

    @Test
    void obtain_returnsAccount() throws Exception {
        when(accountService.find("ACC-001")).thenReturn(sampleResponse());

        mockMvc.perform(get("/cuentas/ACC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC-001"))
                .andExpect(jsonPath("$.clientName").value("John Doe"))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void obtain_returnsNotFoundWhenMissing() throws Exception {
        when(accountService.find("UNKNOWN")).thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(get("/cuentas/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Account not found"));
    }

    @Test
    void create_returnsCreated() throws Exception {
        when(accountService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("ACC-001"))
                .andExpect(jsonPath("$.accountType").value("AHORRO"));
    }

    @Test
    void create_returnsBadRequestWhenAccountNumberIsBlank() throws Exception {
        AccountRequestDTO invalid = new AccountRequestDTO("", AccountType.AHORRO, new BigDecimal("1000.00"), true, "john01");

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void create_returnsBadRequestWhenBalanceIsNegative() throws Exception {
        AccountRequestDTO invalid = new AccountRequestDTO("ACC-001", AccountType.AHORRO, new BigDecimal("-100.00"), true, "john01");

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void create_returnsConflictOnDuplicate() throws Exception {
        when(accountService.create(any())).thenThrow(new DuplicateResourceException("Account number already exists"));

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESOURCE_DUPLICATE"));
    }

    @Test
    void update_returnsUpdatedAccount() throws Exception {
        when(accountService.update(eq("ACC-001"), any())).thenReturn(sampleResponse());

        mockMvc.perform(put("/cuentas/ACC-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC-001"));
    }

    @Test
    void update_returnsNotFoundWhenMissing() throws Exception {
        when(accountService.update(eq("UNKNOWN"), any())).thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(put("/cuentas/UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void patchStatus_disablesAccount() throws Exception {
        AccountResponseDTO disabled = new AccountResponseDTO(1L, "ACC-001", AccountType.AHORRO, new BigDecimal("1000.00"), new BigDecimal("900.00"), false, "john01", "John Doe");
        when(accountService.patchStatus("ACC-001", false)).thenReturn(disabled);

        mockMvc.perform(patch("/cuentas/ACC-001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    void patchStatus_enablesAccount() throws Exception {
        when(accountService.patchStatus("ACC-001", true)).thenReturn(sampleResponse());

        mockMvc.perform(patch("/cuentas/ACC-001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        doNothing().when(accountService).delete("ACC-001");

        mockMvc.perform(delete("/cuentas/ACC-001"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returnsNotFoundWhenMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Account not found")).when(accountService).delete("UNKNOWN");

        mockMvc.perform(delete("/cuentas/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }
}
