package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.type.descriptor.jdbc.TimestampWithTimeZoneJdbcType;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_transaction")
public class Transaction {

    @Id
    @SequenceGenerator(name = "tbl_transaction_generator", sequenceName = "tbl_transaction_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tbl_transaction_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "status")
    private TransactionStatus status;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "timestamp")
    private Timestamp timestamp;

}
