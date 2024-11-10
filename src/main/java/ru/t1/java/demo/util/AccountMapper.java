package ru.t1.java.demo.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.ClientRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AccountMapper {

    private final ClientRepository clientRepository;
    private final TransactionMapper transactionMapper;

    public Account toEntity(AccountDto dto) {
        Client client = clientRepository.findById(dto.getClientId()).orElse(null);

        Account account = Account.builder()
                .client(client)
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .build();

        Set<Transaction> transactionSet = new HashSet<>();
        if (dto.getTransactions() != null) {
            for (TransactionDto transactionDTO: dto.getTransactions()) {
                Transaction createdTransaction = Transaction.builder()
                        .account(account)
                        .time(transactionDTO.getTime())
                        .amount(transactionDTO.getAmount())
                        .build();
                transactionSet.add(createdTransaction);
            }
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
                .transactions(entity.getTransactions().stream().map(transactionMapper::toDto).collect(Collectors.toList()))
                .build();
    }
}
