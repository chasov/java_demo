package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.entity.AccountType;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account extends AbstractPersistable<Long> {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name="id", columnDefinition = "serial")
//    private Long id;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "balance")
    private Long balance;
}
