package dev.rayan.services;

import dev.rayan.model.client.Credential;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public final class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    private final static List<String> ADMIN_EMAILS = new ArrayList<>(List.of("rayan@gmail.com"));

    public String generateToken(final Credential credential) {

        final Set<String> set = createRoles(credential.getEmail());

        return Jwt.issuer(issuer)
                .upn(credential.getEmail())
                .subject(credential.getId().toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(Duration.ofHours(1)))
                .groups(set)
                .claim(Claims.groups.name(), set)
                .sign();
    }

    private Set<String> createRoles(final String email) {
        return (ADMIN_EMAILS.contains(email))
                ? Set.of("admin")
                : Set.of("client");
    }

}