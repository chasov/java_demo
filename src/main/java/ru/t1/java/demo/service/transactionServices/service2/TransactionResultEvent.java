package ru.t1.java.demo.service.transactionServices.service2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.account.AccountStatus;
import ru.t1.java.demo.model.transaction.TransactionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResultEvent {
    private Integer transactionId;
    private Integer accountId;
    private TransactionStatus transactionStatus;
    private AccountStatus accountStatus;

    public TransactionResultEvent(Integer transactionId, Integer accountId, TransactionStatus status) {
    }
}
