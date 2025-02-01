package ru.t1.java.demo.transaction.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.t1.java.demo.account.model.Account;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TransactionAccept {

    private UUID accountUuid;

    private UUID transactionUuid;

    private Timestamp timestamp;

    private BigDecimal transactionAmount;

    private BigDecimal accountBalance;

    public TransactionAccept(Transaction transaction, Account account) {
        this.accountUuid = account.getId();
        this.transactionUuid = transaction.getId();
        this.timestamp = transaction.getTransactionTime();
        this.transactionAmount = transaction.getAmount();
        this.accountBalance = account.getBalance();
    }
}
