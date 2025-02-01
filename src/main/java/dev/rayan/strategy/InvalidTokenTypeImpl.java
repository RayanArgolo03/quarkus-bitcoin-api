package dev.rayan.strategy;


import dev.rayan.exceptions.BusinessException;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
@Priority(1)
public final class InvalidTokenTypeImpl implements TokenStrategy {

    @Override
    public void validateToken(final JsonWebToken token) {
        if (token.getClaim("typ").equals("Bearer")) throw new BusinessException("Invalid token type!");
    }
}
