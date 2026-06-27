package com.devsu.fintech.banking_api.controller;

import com.devsu.fintech.banking_api.dto.ClientRequestDTO;
import com.devsu.fintech.banking_api.dto.ClientResponseDTO;
import com.devsu.fintech.banking_api.excepcion.DuplicateResourceException;
import com.devsu.fintech.banking_api.excepcion.GlobalExceptionHandler;
import com.devsu.fintech.banking_api.excepcion.ResourceNotFoundException;
import com.devsu.fintech.banking_api.service.ClientService;
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

import java.util.List;

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
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController controller;

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

    private ClientResponseDTO sampleResponse() {
        return new ClientResponseDTO(1L, "John Doe", "M", 30, "123456789",
                "123 Main St", "555-0100", "john01", true);
    }

    private ClientRequestDTO sampleRequest() {
        return new ClientRequestDTO("John Doe", "M", 30, "123456789", "123 Main St",
                "555-0100", "john01", "pass1234", true);
    }

    @Test
    void list_returnsOkWithClients() throws Exception {
        when(clientService.list()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientId").value("john01"))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void list_returnsEmptyList() throws Exception {
        when(clientService.list()).thenReturn(List.of());

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getById_returnsClient() throws Exception {
        when(clientService.findByClientId("john01")).thenReturn(sampleResponse());

        mockMvc.perform(get("/clientes/john01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value("john01"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void getById_returnsNotFoundWhenMissing() throws Exception {
        when(clientService.findByClientId("unknown")).thenThrow(new ResourceNotFoundException("Client not found"));

        mockMvc.perform(get("/clientes/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Client not found"));
    }

    @Test
    void create_returnsCreated() throws Exception {
        when(clientService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").value("john01"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void create_returnsBadRequestWhenNameIsBlank() throws Exception {
        ClientRequestDTO invalid = new ClientRequestDTO("", "M", 30, "123456789",
                "123 Main St", "555-0100", "john01", "pass1234", true);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void create_returnsBadRequestWhenPasswordTooShort() throws Exception {
        ClientRequestDTO invalid = new ClientRequestDTO("John Doe", "M", 30, "123456789",
                "123 Main St", "555-0100", "john01", "abc", true);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void create_returnsConflictOnDuplicate() throws Exception {
        when(clientService.create(any())).thenThrow(new DuplicateResourceException("Client already exists"));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESOURCE_DUPLICATE"));
    }

    @Test
    void update_returnsUpdatedClient() throws Exception {
        when(clientService.update(eq("john01"), any())).thenReturn(sampleResponse());

        mockMvc.perform(put("/clientes/john01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value("john01"));
    }

    @Test
    void update_returnsNotFoundWhenMissing() throws Exception {
        when(clientService.update(eq("unknown"), any())).thenThrow(new ResourceNotFoundException("Client not found"));

        mockMvc.perform(put("/clientes/unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void patch_returnsPatchedClient() throws Exception {
        when(clientService.patch(eq("john01"), any())).thenReturn(sampleResponse());

        mockMvc.perform(patch("/clientes/john01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value("john01"));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        doNothing().when(clientService).delete("john01");

        mockMvc.perform(delete("/clientes/john01"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returnsNotFoundWhenMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Client not found")).when(clientService).delete("unknown");

        mockMvc.perform(delete("/clientes/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }
}
