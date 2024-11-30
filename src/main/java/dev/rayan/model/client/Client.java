package dev.rayan.model.client;


import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.rayan.model.bitcoin.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
public class Client {


    //TODO REMOVE SETTER
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "client_id")
    @Setter
    UUID id;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(nullable = false)
    String surname;

    @Column(nullable = false, length = 11)
    String cpf;

    @Embedded
    Adress adress;

    @Column(name = "user_name", nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String password;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    Set<Transaction> transactions;

    @Column(name = "created_at")
    LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_update")
    @UpdateTimestamp
    LocalDateTime lastUpdate = null;

}
