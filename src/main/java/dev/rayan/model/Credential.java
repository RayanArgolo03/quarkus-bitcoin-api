package dev.rayan.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "credentials")
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "credential_id")
    @JsonIgnore
    UUID id;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    @JsonIgnore
    String password;

    @OneToOne(mappedBy = "credential", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    final Client client = null;

    @Column(name = "created_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    final LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    LocalDateTime updatedAt = null;

    @PreUpdate
    private void setUpdatedAt() { updatedAt = LocalDateTime.now(); }
}
