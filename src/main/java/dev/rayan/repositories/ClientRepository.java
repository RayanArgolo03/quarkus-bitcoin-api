package dev.rayan.repositories;

import dev.rayan.dto.request.client.ClientsByCreatedAtRequest;
import dev.rayan.dto.request.client.ClientsByAddressFilterRequest;
import dev.rayan.dto.request.client.UpdateClientRequest;
import dev.rayan.dto.response.client.FoundClientResponse;
import dev.rayan.model.Address;
import dev.rayan.model.Client;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public final class ClientRepository implements PanacheRepositoryBase<Client, UUID> {

    private static final String FIND_CLIENT_QUERY = """
                SELECT firstName, surname, birthDate, cpf, email, address
                FROM Client c
                JOIN c.credential cc
            """;


    public Optional<String> findCpf(final String cpf) {
        return find("SELECT cpf FROM Client WHERE cpf = ?1", cpf)
                .project(String.class)
                .singleResultOptional();
    }


    public PanacheQuery<FoundClientResponse> findClientsByCreatedAt(final ClientsByCreatedAtRequest request) {

        final StringBuilder sb = new StringBuilder(FIND_CLIENT_QUERY);

        sb.append("""
                WHERE DATE(c.createdAt) BETWEEN :startDate AND :endDate
                AND c.updatedAt
                """);

        sb.append(request.hasUpdated() ? " IS NOT NULL" : " IS NULL");

        final Parameters parameters = Parameters.with("startDate", request.getStartDate())
                .and("endDate", request.getEndDate());

        final Sort sort = Sort.by("firstName", request.getSortFirstName());

        return find(sb.toString(), sort, parameters)
                .project(FoundClientResponse.class);
    }

    public List<FoundClientResponse> findClientsByAddressFilter(final ClientsByAddressFilterRequest request) {
        return null;
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
