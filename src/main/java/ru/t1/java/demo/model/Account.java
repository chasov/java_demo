package ru.t1.java.demo.model;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Integer> {
//    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
//    private Client client;
    private Long client_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "balance")
    Double balance;

//    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Transaction> transactions = new ArrayList<>();

}