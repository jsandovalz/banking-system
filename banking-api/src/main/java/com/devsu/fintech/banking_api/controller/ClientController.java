package com.devsu.fintech.banking_api.controller;

import com.devsu.fintech.banking_api.dto.ClientRequestDTO;
import com.devsu.fintech.banking_api.dto.ClientResponseDTO;
import com.devsu.fintech.banking_api.service.ClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "CRUD de clientes")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public List<ClientResponseDTO> list() {
        return clientService.list();
    }

    @GetMapping("/{clientId}")
    public ClientResponseDTO getById(@PathVariable String clientId) {
        return clientService.findByClientId(clientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponseDTO create(@Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        return clientService.create(clientRequestDTO);
    }

    @PutMapping("/{clientId}")
    public ClientResponseDTO update(@PathVariable String clientId,
                                    @Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        return clientService.update(clientId,clientRequestDTO);
    }

    @PatchMapping("/{clientId}")
    public ClientResponseDTO patch(@PathVariable String clientId,
                                   @Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        return clientService.patch(clientId,clientRequestDTO);
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> delete(@PathVariable String clientId) {
        clientService.delete(clientId);
        return ResponseEntity.noContent().build();
    }

}

