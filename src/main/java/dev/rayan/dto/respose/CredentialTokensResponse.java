package dev.rayan.dto.respose;

public record CredentialTokensResponse(
        String accessToken,
        String refreshToken,
        String issuedAt,
        String expiresIn) {

}
