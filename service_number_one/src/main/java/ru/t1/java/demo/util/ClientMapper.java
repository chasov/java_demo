package ru.t1.java.demo.util;

import ru.t1.java.demo.dto.ClientDTO;
import ru.t1.java.demo.model.Client;


public class ClientMapper {

    private ClientMapper() {
    }


    public static Client toEntity(ClientDTO dto) {
        return Client.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .build();
    }

    public static ClientDTO toDTO(Client entity) {
        return ClientDTO.builder()
                .clientId(entity.getClientId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .build();
    }

}
