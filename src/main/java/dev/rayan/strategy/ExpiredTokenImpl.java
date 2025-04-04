package dev.rayan.strategy;


import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

@RequestScoped
@Priority(0)
public final class ExpiredTokenImpl implements TokenStrategy {

    @Override
    public void validateToken(final JsonWebToken token) {
        if (tokenIsExpired(token.getExpirationTime())) {
            throw new NotAuthorizedException("Desconnected, you need to login again!", UNAUTHORIZED);
        }
    }

    private boolean tokenIsExpired(final long expirationTime) {

        final LocalDateTime expirationDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(expirationTime),
                ZoneId.systemDefault()
        );

        return expirationDateTime.isBefore(LocalDateTime.now());
    }
}
