package dev.rayan.repositories;

import dev.rayan.model.client.Credential;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public final class CredentialRepository implements PanacheRepositoryBase<Credential, UUID> {

    public Optional<Credential> findCredential(final String email) {
        return find("LOWER(email)", email).singleResultOptional();
    }
}
