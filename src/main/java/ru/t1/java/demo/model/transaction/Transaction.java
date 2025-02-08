package ru.t1.java.demo.model.transaction;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.account.Account;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction extends AbstractPersistable<Long> {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", nullable = true)
    private Account account;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "date")
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;

    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "transaction_time")
    private Timestamp transactionTime;

    public Transaction(Integer transactionId, Account account, BigDecimal amount, TransactionStatus status) {
    }
}
