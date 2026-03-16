package dev.rayan.dto.response.client;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

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
