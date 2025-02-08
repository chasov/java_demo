package ru.t1.java.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import ru.t1.java.demo.model.enums.TransactionStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonProperty("account_id")
    private Account account;

    private BigDecimal amount;

    private LocalDateTime transactionTime;

    @Column(name = "transaction_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatusEnum transactionStatus=TransactionStatusEnum.REQUESTED;

    @PrePersist
    public void prePersist() {
        this.transactionTime = LocalDateTime.now();
    }
}
