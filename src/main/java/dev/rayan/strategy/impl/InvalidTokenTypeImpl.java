package dev.rayan.strategy.impl;


import dev.rayan.exceptions.BusinessException;
import dev.rayan.strategy.TokenStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public final class InvalidTokenTypeImpl implements TokenStrategy {

    @Override
    public void validateToken(final JsonWebToken token) {
        if (!token.getClaim("typ").equals("Refresh")) throw new BusinessException("Invalid token type!");
    }
}
