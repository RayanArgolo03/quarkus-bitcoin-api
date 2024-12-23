package dev.rayan.model.client;


import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.rayan.model.bitcoin.Transaction;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
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

@UserDefinition
@Entity
@Table(name = "clients")
public class Client {

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

    @Column(nullable = false, unique = true)
    String email;

    @Embedded
    Address address;

    @Username
    @Column(name = "username", nullable = false, unique = true)
    String username;

    @Password
    @Column(nullable = false)
    String password;

    @Roles
    @Column(nullable = false)
    String roles;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    Set<Transaction> transactions;

    @Column(name = "created_at")
    LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt = null;

    public static void setRoleAndEncryptPassword(final Client client) {
        client.password = BcryptUtil.bcryptHash(client.password);

        client.roles = (client.username.equals("admin"))
                ? "admin"
                : "user";
    }


}
