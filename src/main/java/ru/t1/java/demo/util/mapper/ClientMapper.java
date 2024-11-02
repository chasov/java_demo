package ru.t1.java.demo.util.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ClientMapper {

    private final AccountMapper accountMapper;

    public Client toEntity(ClientDto dto) {
        Client client = Client.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .build();

        List<Account> accountList = new ArrayList<>();
        if (dto.getAccounts() != null) {
            for (AccountDto accountDto: dto.getAccounts()) {
                Account createdAccount = Account.builder()
                        .client(client)
                        .balance(accountDto.getBalance())
                        .accountType(accountDto.getAccountType())
                        .build();
                accountList.add(createdAccount);

                Set<Transaction> transactionSet = new HashSet<>();
                for (TransactionDto transactionDTO: accountDto.getTransactions()) {
                    Transaction createdTransaction = Transaction.builder()
                            .account(createdAccount)
                            .time(transactionDTO.getTime())
                            .amount(transactionDTO.getAmount())
                            .build();
                    transactionSet.add(createdTransaction);
                }
                createdAccount.setTransactions(transactionSet);
                transactionSet.forEach(transaction -> transaction.setAccount(createdAccount));
            }
        }

        client.setAccounts(accountList);
        accountList.forEach(account -> account.setClient(client));

        return client;
    }

    public ClientDto toDto(Client entity) {
        return ClientDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .accounts(entity.getAccounts().stream().map(accountMapper::toDto).collect(Collectors.toList()))
                .build();
    }

}
