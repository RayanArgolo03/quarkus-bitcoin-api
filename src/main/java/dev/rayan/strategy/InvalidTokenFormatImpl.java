package dev.rayan.strategy;


import dev.rayan.exceptions.BusinessException;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
@Priority(2)
public final class InvalidTokenFormatImpl implements TokenStrategy {

    @Override
    public void validateToken(final JsonWebToken token) {
        if (token == null) throw new BusinessException("Invalid token format!");
    }
}
