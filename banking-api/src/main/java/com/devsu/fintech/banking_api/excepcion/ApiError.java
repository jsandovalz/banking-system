package com.devsu.fintech.banking_api.excepcion;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ApiError {
    private String code;
    private String message;
    private int status;
    private String path;
    private List<String> details;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
