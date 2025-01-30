package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.enums.TransactionState;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction extends AbstractPersistable<Long> {

    @Column(name = "account_id")
    Long accountId;

    @Column(name = "amount")
    BigDecimal amount;

    @Column(name = "timestamp")
    Timestamp timestamp;

    @Column(name = "transaction_id")
    private Integer transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20)
    private TransactionState state;

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

}
