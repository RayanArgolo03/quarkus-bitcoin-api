package dev.rayan.model.client;


import dev.rayan.model.bitcoin.Transaction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(nullable = false)
    String surname;

    @Column(columnDefinition = "VARCHAR(11) NOT NULL UNIQUE")
    String cpf;

    @Embedded
    Adress adress;

    @Column(name = "user_name", nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String password;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "client_id")
    Set<Transaction> transactions;


}
