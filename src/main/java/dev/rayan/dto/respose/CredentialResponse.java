package dev.rayan.dto.respose;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.time.LocalDateTime;
import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public final class CredentialResponse {

    UUID id;
    String email;
    String password;
    LocalDateTime createdAt;

    @NonFinal
    @Setter
    String keycloakUserId;
}
