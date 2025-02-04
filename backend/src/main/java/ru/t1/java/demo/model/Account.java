package ru.t1.java.demo.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import ru.t1.java.demo.model.enums.AccountTypeEnum;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonProperty("client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    private AccountTypeEnum accountType;

    private BigDecimal balance;

/*    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Transaction> transactions;*/
}
