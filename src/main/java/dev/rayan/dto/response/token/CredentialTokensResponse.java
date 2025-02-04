package dev.rayan.dto.response.token;

public record CredentialTokensResponse(
        String accessToken,
        String refreshToken,
        String issuedAt,
        String expiresIn) {

}
