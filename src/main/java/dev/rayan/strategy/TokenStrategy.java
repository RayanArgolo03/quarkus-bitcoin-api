package dev.rayan.strategy;

import org.eclipse.microprofile.jwt.JsonWebToken;


public interface TokenStrategy {
    void validateToken(JsonWebToken token);
}
