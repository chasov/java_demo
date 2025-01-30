package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.web.CheckWebClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

//    @Spy
//    ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    @Spy
    ClientServiceImpl clientService;

    @Mock
    ClientServiceImpl clientServiceMock;

    @Mock
    ClientRepository clientRepositoryMock;

    @Mock
    KafkaClientProducer kafkaClientProducer;

    @Mock
    CheckWebClient checkWebClient;



    @Test
    void registerClientTest() {

        Client client = new Client();
        client.setClientId(42);
        client.setFirstName("John");
        client.setLastName("Doe");

        Client client2 = new Client();
        client2.setId(422222L);
        client2.setFirstName("John");
        client2.setLastName("Doe");

        when(clientRepositoryMock.save(client)).thenReturn(client2);

        doNothing()
                .when(kafkaClientProducer)
                .send(anyLong());

        when(checkWebClient.check(any()))
                .thenReturn(Optional.of(CheckResponse.builder()
                        .blocked(false).build()));

        List<Client> clients = clientService.registerClients(List.of(client));

        assertThat(clients.get(0).getId()).isEqualTo(422222L);

    }
}