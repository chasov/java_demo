package ru.t1.java.demo.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import ru.t1.java.demo.model.enums.AccountStatusEnum;
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

    @Column(nullable = false,name = "account_status")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AccountStatusEnum accountStatus = AccountStatusEnum.OPEN;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal frozenAmount= BigDecimal.valueOf(0);

/*    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Transaction> transactions;*/
}
