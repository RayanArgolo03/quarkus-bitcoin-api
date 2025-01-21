package dev.rayan.strategy.impl;


import dev.rayan.strategy.TokenStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.Reader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

@ApplicationScoped
public final class ExpiredTokenImpl implements TokenStrategy {

    @Override
    public void validateToken(final JsonWebToken token) {
        if (tokenIsExpired(token.getExpirationTime())) {
            throw new NotAuthorizedException("Desconnected, you need to login again!", 401, UNAUTHORIZED);
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
