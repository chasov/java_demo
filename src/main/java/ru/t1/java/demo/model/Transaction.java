package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction extends AbstractPersistable<Long> {
//    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
//    private Account account;
    private Long account_id;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

}