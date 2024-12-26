package dev.rayan.model.client;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "credentials")
public final class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "credential_id")
    UUID id;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String password;

    public Credential(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static void setEncryptPassword(final Credential credential) {
       credential.password  = BcryptUtil.bcryptHash(credential.password);
    }

}
