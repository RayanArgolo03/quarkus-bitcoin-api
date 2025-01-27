package dev.rayan.strategy.impl;


import dev.rayan.exceptions.BusinessException;
import dev.rayan.strategy.TokenStrategy;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import net.bytebuddy.build.AccessControllerPlugin;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
@Priority(2)
public final class InvalidTokenFormatImpl implements TokenStrategy {

    @Override
    public void validateToken(final JsonWebToken token) {
        if (token == null) throw new BusinessException("Invalid token format!");
    }
}
