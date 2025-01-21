package ru.t1.java.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Transaction extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private BigDecimal amount;

    private LocalDateTime date;
}
