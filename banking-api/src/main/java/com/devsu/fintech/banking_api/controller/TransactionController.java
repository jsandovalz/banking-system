package com.devsu.fintech.banking_api.controller;

import com.devsu.fintech.banking_api.dto.TransactionRequestDTO;
import com.devsu.fintech.banking_api.dto.TransactionResponseDTO;
import com.devsu.fintech.banking_api.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Registro de depositos y retiros")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionResponseDTO> list(@RequestParam(required = false) String accountNumber) {
        return accountNumber == null
                ? transactionService.list()
                : transactionService.listByAccount(accountNumber);
    }

    @GetMapping("/{id}")
    public TransactionResponseDTO obtain(@PathVariable Long id) {
        return transactionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDTO register(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        return transactionService.register(transactionRequestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
