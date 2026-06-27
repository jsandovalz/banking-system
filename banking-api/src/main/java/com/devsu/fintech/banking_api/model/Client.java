package com.devsu.fintech.banking_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Client extends Person{
    @NotBlank(message = "Client ID is mandatory")
    @Size(max = 30)
    @Column(name = "client_id_external", nullable = false, unique = true, length = 30)
    private String clientId;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 4, max = 100)
    @Column(nullable = false, length = 100)
    private String password;

    @NotNull(message = "Status is mandatory")
    @Column(nullable = false)
    private Boolean status;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

}

