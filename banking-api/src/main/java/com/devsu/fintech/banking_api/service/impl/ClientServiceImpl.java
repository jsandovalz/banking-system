package com.devsu.fintech.banking_api.service.impl;

import com.devsu.fintech.banking_api.dto.ClientRequestDTO;
import com.devsu.fintech.banking_api.dto.ClientResponseDTO;
import com.devsu.fintech.banking_api.excepcion.DuplicateResourceException;
import com.devsu.fintech.banking_api.excepcion.ResourceNotFoundException;
import com.devsu.fintech.banking_api.mapper.ClientMapper;
import com.devsu.fintech.banking_api.model.Client;
import com.devsu.fintech.banking_api.repository.ClientRepository;
import com.devsu.fintech.banking_api.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDTO create(ClientRequestDTO dto) {
        if(clientRepository.existsByClientId(dto.clientId())) {
            throw new DuplicateResourceException("Already exists a client with id " + dto.clientId());
        }
        if(clientRepository.existsByIdentification(dto.identification())) {
            throw new DuplicateResourceException("Already exists a person identify with " + dto.identification());
        }

        Client client = clientMapper.toEntity(dto);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Override
    public ClientResponseDTO update(String clientId, ClientRequestDTO dto) {
        Client client = getClient(clientId);
        client.setName(dto.name());
        client.setGender(dto.gender());
        client.setAge(dto.age());
        client.setIdentification(dto.identification());
        client.setAddress(dto.address());
        client.setPhone(dto.phone());
        client.setPassword(dto.password());
        client.setStatus(dto.status());
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Override
    public ClientResponseDTO patch(String clientId, ClientRequestDTO dto) {
        Client client = getClient(clientId);
        clientMapper.updateEntity(dto, client);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Override
    public void delete(String clientId) {
        Client client = getClient(clientId);
        clientRepository.delete(client);
    }

    @Override
    public ClientResponseDTO findByClientId(String clientId) {
        return clientMapper.toResponse(getClient(clientId));
    }

    @Override
    public List<ClientResponseDTO> list() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .toList();
    }
    private Client getClient(String clientId) {
        return clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + clientId));
    }
}
