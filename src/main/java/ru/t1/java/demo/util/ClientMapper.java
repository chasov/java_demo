package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;

@Component
public class ClientMapper {

    public static Client toEntity(ClientDto dto) {
        if (dto == null) {
            return null;
        }

        return Client.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .clientId(dto.getClientId())
                .build();
    }

    public static ClientDto toDto(Client entity) {
        if (entity == null) {
            return null;
        }

        return ClientDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .clientId(entity.getClientId())
                .build();
    }
}
