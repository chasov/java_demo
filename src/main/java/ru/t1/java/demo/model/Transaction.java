package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.account.Account;

import java.math.BigDecimal;
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
    @JoinColumn(name = "account_id", nullable = true)
    private Account account;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "date")
    private LocalDateTime dateTime;
}
