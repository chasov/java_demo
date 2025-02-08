package ru.t1.java.demo.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.fraud_serviceDto.FraudServiceTransactionDto;
import ru.t1.java.demo.dto.transaction_serviceDto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.AccountService;

@Mapper(componentModel = "spring", uses = TransactionMapper.AccountResolver.class)
public interface TransactionMapper {

    @Mapping(source = "account.id", target = "account_id")
    TransactionDto toDto(Transaction transaction);

    @Mapping(source = "account.id",target = "accountId")
    @Mapping(source = "account.client.id",target = "clientId")
    @Mapping(source = "account.balance",target = "balance")
    @Mapping(source = "id",target = "transactionId")
    FraudServiceTransactionDto toFraudServiceDto(Transaction transaction);

    @Mapping(source = "account_id", target = "account", qualifiedByName = "mapAccount")
    Transaction toEntity(TransactionDto transactionDto);


    @RequiredArgsConstructor
    @Component
    class AccountResolver {
        private final AccountService accountService;

        @Named("mapAccount")
        public Account mapAccount(Long accountId) {
            return accountId == null ? null : accountService.getAccountById(accountId);
        }
    }
}
