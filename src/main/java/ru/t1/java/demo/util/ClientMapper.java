package ru.t1.java.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.model.Client;

@Component
@Slf4j
public class ClientMapper {

    public static Client toEntity(ClientDto dto) {
        if (dto.getMiddleName() == null) {
//            throw new NullPointerException();
        }
        return Client.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .build();
    }

    public static ClientDto toDto(Client entity) {
        return ClientDto.builder()
                // .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .build();
    }

    //    @ReplaceResult
    public Client toEntityWithId(ClientDto dto) {
        log.info("Mapping to entity with id if exist");
//        if (dto.getMiddleName() == null) {
//            throw new NullPointerException();
//        }
        int randomInt = (int) (Math.random() * 100000000);
        return Client.builder()
                .clientId(randomInt)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .build();
    }
}
