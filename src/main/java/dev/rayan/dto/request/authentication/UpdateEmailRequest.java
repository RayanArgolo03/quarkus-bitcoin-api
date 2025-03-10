package dev.rayan.dto.request.authentication;

public record UpdateEmailRequest(
        String currentEmail,
        String newEmail,
        String confirmedNewEmail
) {
}