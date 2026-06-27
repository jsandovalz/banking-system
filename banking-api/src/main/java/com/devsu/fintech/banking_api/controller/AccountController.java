package com.devsu.fintech.banking_api.controller;

import com.devsu.fintech.banking_api.dto.AccountRequestDTO;
import com.devsu.fintech.banking_api.dto.AccountResponseDTO;
import com.devsu.fintech.banking_api.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "CRUD de cuentas bancarias")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountResponseDTO> list(@RequestParam(required = false) String cliendId) {
        return cliendId == null
                ? accountService.list()
                : accountService.listByClient(cliendId);
    }

    @GetMapping("/{accountNumber}")
    public AccountResponseDTO obtain(@PathVariable String accountNumber) {
        return accountService.find(accountNumber);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponseDTO create(@Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        return accountService.create(accountRequestDTO);
    }

    @PutMapping("/{accountNumber}")
    public AccountResponseDTO update(@PathVariable String accountNumber,
                                     @Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        return accountService.update(accountNumber,accountRequestDTO);
    }

    @PatchMapping("/{accountNumber}/status")
    public AccountResponseDTO path(@PathVariable String accountNumber,
                                   @RequestBody Map<String, Boolean> body) {
        return accountService.patchStatus(accountNumber,body.get("status"));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> delete(@PathVariable String accountNumber) {
        accountService.delete(accountNumber);
        return ResponseEntity.noContent().build();
    }
}

