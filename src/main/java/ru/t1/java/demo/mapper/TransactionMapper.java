package ru.t1.java.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.entity.Transaction;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(source = "account.id", target = "accountId")
    TransactionDto toDto(Transaction transaction);

    @Mapping(target = "account", ignore = true)
    Transaction toEntity(TransactionDto transactionDto);
}
