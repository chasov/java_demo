package ru.t1.java.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.t1.java.demo.model.enums.TransactionalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactional")
public class Transactional {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID transactionalId;
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    Account account;

    @NotNull
    private BigDecimal priceTransactional;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionalStatus transactionalStatus;
    @NotNull
    //время выполнения транзакции ms
    private Long timeTransactional;
    @NotNull
    @PastOrPresent
    //время когда была произведена транзакция
    private LocalDateTime timestamp;
}
