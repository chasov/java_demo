package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.entity.Client;

import java.util.UUID;

@Component
public class ClientMapper {

    public static Client toEntity(ClientDto dto) {
        if (dto.getMiddleName() == null) {
//            throw new NullPointerException();
        }
        Client client = Client.builder()
                .id(dto.getId())    //что бы при перезапуске перезаписывались существующие записи, а не добавлялись новые
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .build();
        if (client.getClientId() == null) {
            client.setClientId(UUID.randomUUID());
        }
        return client;
    }

    public static ClientDto toDto(Client entity) {
        return ClientDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .build();
    }

}
