package dev.rayan.repositories;

import dev.rayan.model.client.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public final class ClientRepository implements PanacheRepositoryBase<Client, UUID> {

    public Optional<String> findCpf(final String cpf) {
        return find("SELECT cpf FROM Client WHERE cpf = ?1", cpf)
                .project(String.class)
                .singleResultOptional();
    }
}
