package com.devsu.fintech.banking_api.repository;

import com.devsu.fintech.banking_api.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByClientId(String clientId);
    boolean existsByIdentification(String identification);
    Optional<Client> findByClientId(String clientId);
    Optional <Client> findByIdentification(String identification);
}