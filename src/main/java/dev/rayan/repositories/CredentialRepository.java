package dev.rayan.repositories;

import dev.rayan.model.Credential;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public final class CredentialRepository implements PanacheRepositoryBase<Credential, UUID> {

    public Optional<Credential> findCredentialByEmail(final String email) {
        return find("email", email)
                .singleResultOptional();
    }
}
