package dev.rayan.model;


import dev.rayan.enums.TransactionType;
import dev.rayan.model.Client;
import io.quarkus.arc.All;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter

@Entity
@Table(name = "transactions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@DynamicInsert
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    UUID id;

    @Column(nullable = false)
    BigDecimal quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", nullable = false)
    Client client;

    @Enumerated(value = EnumType.STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    TransactionType type;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();

}
