package dev.rayan.model.bitcoin;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Entity
@Table(name = "transactions")
@DynamicInsert
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "quantity", columnDefinition = "DECIMAL(10, 2) NOT NULL")
    Double bitcoinQuantity;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

}
