package dev.rayan.repositories;

import dev.rayan.dto.request.UpdateClientRequest;
import dev.rayan.model.client.Address;
import dev.rayan.model.client.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public final class ClientRepository implements PanacheRepositoryBase<Client, UUID> {

    public Optional<String> findCpf(final String cpf) {
        return find("SELECT cpf FROM Client WHERE cpf = ?1", cpf)
                .project(String.class)
                .singleResultOptional();
    }

    public void updatePartial(final Client client, final UpdateClientRequest request) {
        client.setFirstName(request.firstName());
        client.setSurname(request.surname());
        persist(client);
    }

    public void updateAddress(final Client client, final Address address) {
        client.setAddress(address);
        persist(client);
    }
}
