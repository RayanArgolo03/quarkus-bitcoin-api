package dev.rayan.dto.response.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.time.LocalDateTime;
import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Getter
public final class CredentialResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    UUID id;
    String email;
    @JsonIgnore
    String password;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime createdAt;

    @NonFinal
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String keycloakUserId;
}
