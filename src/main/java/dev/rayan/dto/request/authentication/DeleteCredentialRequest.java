package dev.rayan.dto.request.authentication;

import jakarta.ws.rs.PathParam;
import org.hibernate.validator.constraints.UUID;

public record DeleteCredentialRequest(
        @PathParam("id")
        @UUID(message = "Invalid id!")
        String id
) {}
