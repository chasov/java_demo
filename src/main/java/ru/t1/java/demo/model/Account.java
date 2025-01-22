package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account extends AbstractPersistable<Long> {
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "balance")
    private Long balance;
}
