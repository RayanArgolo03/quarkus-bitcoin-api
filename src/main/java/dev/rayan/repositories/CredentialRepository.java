package dev.rayan.repositories;

import dev.rayan.model.Credential;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public final class CredentialRepository implements PanacheRepositoryBase<Credential, UUID> {

    public Optional<Credential> findCredential(final String email) {
        return find("email", email)
                .singleResultOptional();
    }

    public Optional<String> findCredentialPassword(final String email) {
        final String query = "SELECT password FROM Credential WHERE LOWER(email) = ?1";
        return find(query, email)
                .project(String.class)
                .singleResultOptional();
    }
}
