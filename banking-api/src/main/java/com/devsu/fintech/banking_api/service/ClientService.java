package com.devsu.fintech.banking_api.service;

import com.devsu.fintech.banking_api.dto.ClientRequestDTO;
import com.devsu.fintech.banking_api.dto.ClientResponseDTO;

import java.util.List;

public interface ClientService {
    ClientResponseDTO create(ClientRequestDTO dto);
    ClientResponseDTO update(String clientId, ClientRequestDTO dto);
    ClientResponseDTO patch(String clientId, ClientRequestDTO dto);
    void delete(String clientId);
    ClientResponseDTO findByClientId(String clientId);
    List<ClientResponseDTO> list();
}
