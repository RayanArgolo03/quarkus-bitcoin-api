package dev.rayan.dto.respose;

import java.time.LocalDateTime;
import java.util.UUID;

public record CredentialResponse(UUID id,
                                 String email,
                                 String password,
                                 LocalDateTime createdAt
) {
}
