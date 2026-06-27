package com.devsu.fintech.banking_api.service;

import com.devsu.fintech.banking_api.dto.ClientRequestDTO;
import com.devsu.fintech.banking_api.excepcion.DuplicateResourceException;
import com.devsu.fintech.banking_api.excepcion.ResourceNotFoundException;
import com.devsu.fintech.banking_api.mapper.ClientMapper;
import com.devsu.fintech.banking_api.model.Client;
import com.devsu.fintech.banking_api.repository.ClientRepository;
import com.devsu.fintech.banking_api.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientServiceImpl")
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl service;

    private ClientRequestDTO mockDTO() {
        return new ClientRequestDTO("Jose Lema", "M", 30, "0102030405",
                "Otavalo sn y principal", "098254785", "joselema", "1234", true);
    }

    @Test
    void create_throws_duplicate_when_clientId_exists() {
        String clientId="joselema";
        when(clientRepository.existsByClientId(clientId)).thenReturn(true);
        assertThatThrownBy(() -> service.create(mockDTO()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Already exists a client with id "+ clientId);
    }

    @Test
    void find_throws_not_found_when_clientId_does_not_exist() {
        when(clientRepository.findByClientId("xxx")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findByClientId("xxx"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Client not found: xxx");
    }

    @Test
    void create_saves_when_no_duplicate() {
        ClientRequestDTO dto = mockDTO();
        Client client = Client.builder()
                .clientId("joselema")
                .name("Jose Lema")
                .gender("M")
                .age(30)
                .identification("0102030405")
                .address("Otavalo sn y principal")
                .phone("098254785")
                .password("1234")
                .status(true)
                .build();

        when(clientRepository.existsByClientId("joselema")).thenReturn(false);
        when(clientRepository.existsByIdentification("0102030405")).thenReturn(false);
        when(clientMapper.toEntity(dto)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);

        service.create(dto);
        verify(clientRepository).save(client);

    }


}
