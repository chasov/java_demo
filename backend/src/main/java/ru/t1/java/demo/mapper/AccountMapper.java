package ru.t1.java.demo.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.service.ClientService;

@Mapper(componentModel = "spring", uses = AccountMapper.ClientResolver.class)
public interface AccountMapper {

    @Mapping(source = "client.id", target = "client_id")
    AccountDto toDto(Account account);

    @Mapping(source = "client_id", target = "client", qualifiedByName = "mapClient")
    Account toEntity(AccountDto accountDto);

    @RequiredArgsConstructor
    @Component
    class ClientResolver {
        private final ClientService clientService;

        @Named("mapClient")
        public Client mapClient(Long clientId) {
            return clientId == null ? null : clientService.getClientById(clientId);
        }
    }
}
