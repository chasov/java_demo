package ru.t1.java.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.entity.Account;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(source = "client.id", target = "clientId")
    AccountDto toDto(Account account);

    @Mapping(target = "client", ignore = true)
    Account toEntity(AccountDto accountDto);
}
