package dev.rayan.model.bitcoin;


import dev.rayan.model.client.Client;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter

@Entity
@Table(name = "transactions")
@DynamicInsert
public class Transaction extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    Client client;

    @Column(name = "quantity", nullable = false)
    float bitcoinQuantity;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    public Transaction(float bitcoinQuantity, Client client) {
        this.bitcoinQuantity = bitcoinQuantity;
        this.client = client;
    }
}
