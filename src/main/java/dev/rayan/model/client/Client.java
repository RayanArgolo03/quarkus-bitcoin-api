package dev.rayan.model.client;


import dev.rayan.model.bitcoin.Transaction;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "clients")
public class Client {

    @Id
    @Column(name = "credential_id")
    UUID id;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(nullable = false)
    String surname;

    @Column(name = "birth_date", nullable = false)
    final LocalDate birthDate;

    @Column(nullable = false, length = 11, unique = true)
    final String cpf;

    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "credential_id")
    final Credential credential;

    @Embedded
    Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Transaction> transactions;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();

    @NonFinal
    @Column(name = "updated_at")
    LocalDateTime updatedAt = null;

    @PreUpdate
    private void setUpdatedAt() {
        updatedAt = LocalDateTime.now();
    }

}
