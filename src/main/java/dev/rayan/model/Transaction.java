package dev.rayan.model;


import dev.rayan.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "transactions")
@DynamicInsert
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.PRIVATE)
    @Column(name = "transaction_id")
    UUID id;

    @Column(nullable = false)
    final BigDecimal quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", nullable = false)
    final Client client;

    @Enumerated(value = EnumType.STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    final TransactionType type;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();

}
