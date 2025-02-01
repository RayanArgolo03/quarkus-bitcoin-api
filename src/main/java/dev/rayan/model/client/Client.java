package dev.rayan.model.client;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "clients")
public class Client {

    @Id
    @Column(name = "credential_id")
    UUID id;

    @NonFinal
    @Column(name = "first_name", nullable = false)
    String firstName;

    @NonFinal
    @Column(nullable = false)
    String surname;

    @Column(name = "birth_date", nullable = false)
    LocalDate birthDate;

    @Column(nullable = false, length = 11, unique = true)
    String cpf;

    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "credential_id")
    Credential credential;

    @NonFinal
    @Embedded
    Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    Set<Transaction> transactions;

    @Column(name = "created_at")
    LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt = LocalDateTime.now();
}
