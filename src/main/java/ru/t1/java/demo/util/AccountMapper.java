package ru.t1.java.demo.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.ClientRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AccountMapper {
    private final ClientRepository clientRepository;
    private final TransactionMapper transactionMapper;

    public Account toEntity(AccountDto dto) throws AccountException, ClientException {
        Long clientId = dto.getClientId();
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            throw new ClientException(String.format("Client with id %s is not exists", clientId));
        }
        Account account = Account.builder()
                .client(client.get())
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .build();

        Set<Transaction> transactionSet = new HashSet<>();
        if (dto.getTransactions() != null) {
            transactionSet = dto.getTransactions().stream().map(transactionDto -> Transaction.builder()
                                                                                             .account(account)
                                                                                             .time(transactionDto.getTimestamp())
                                                                                             .amount(transactionDto.getAmount())
                                                                                             .build())
                                                           .collect(Collectors.toSet());
        }
        account.setTransactions(transactionSet);
        transactionSet.forEach(transaction -> transaction.setAccount(account));
        return account;
    }

    public AccountDto toDto(Account entity) {
        return AccountDto.builder()
                         .id(entity.getId())
                         .clientId(entity.getClient().getId())
                         .accountType(entity.getAccountType())
                         .balance(entity.getBalance())
                         .transactions(entity.getTransactions().stream().map(transactionMapper::toDto).toList())
                         .build();
    }
}
