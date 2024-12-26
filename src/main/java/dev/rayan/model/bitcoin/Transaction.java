package dev.rayan.model.bitcoin;


import dev.rayan.enums.TransactionType;
import dev.rayan.model.client.Client;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "transactions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@DynamicInsert
public final class Transaction extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    Client client;

    @Column(nullable = false)
    BigDecimal quantity;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(value = EnumType.STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    TransactionType type;

    public Transaction(BigDecimal quantity, Client client, TransactionType type) {
        this.quantity = quantity;
        this.client = client;
        this.type = type;
    }
}
