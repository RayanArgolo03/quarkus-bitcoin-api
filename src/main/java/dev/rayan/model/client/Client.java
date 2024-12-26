package dev.rayan.model.client;


import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.rayan.model.bitcoin.Transaction;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "clients")
public final class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "client_id")
    UUID id;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(nullable = false)
    String surname;

    @Column(name = "birth_date", nullable = false)
    LocalDate birthDate;

    @Column(nullable = false, length = 11, unique = true)
    String cpf;

    @Embedded
    Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    Set<Transaction> transactions;

    @Column(name = "created_at")
    LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt = null;


}
