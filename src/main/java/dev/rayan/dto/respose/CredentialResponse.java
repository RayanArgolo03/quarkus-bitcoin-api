package dev.rayan.dto.respose;

import java.util.UUID;

public record CredentialResponse(UUID id, String token) {
}
