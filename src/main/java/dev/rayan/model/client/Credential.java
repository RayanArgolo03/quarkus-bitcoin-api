package dev.rayan.model.client;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "credentials")
@DynamicInsert
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "client_id")
    UUID id;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String password;

    @OneToOne(mappedBy = "credential", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    Client client;

    @Column(name = "created_at")
    final LocalDateTime createdAt = LocalDateTime.now();

    public Credential(String email, String password) {
        this.email = email;
        this.password = password;
    }


}
