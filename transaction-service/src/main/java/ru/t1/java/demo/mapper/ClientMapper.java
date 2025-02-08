package ru.t1.java.demo.mapper;

import org.mapstruct.Mapper;
import ru.t1.java.demo.dto.transaction_serviceDto.ClientDto;
import ru.t1.java.demo.model.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toEntity(ClientDto clientDto);

    ClientDto toDto(Client client);
}
