package dev.rayan.repositories;

import dev.rayan.dto.request.client.ClientsByCreatedAtRequest;
import dev.rayan.dto.request.client.ClientsByStateRequest;
import dev.rayan.dto.request.client.UpdateClientRequest;
import dev.rayan.dto.response.client.ClientByCreatedAtResponse;
import dev.rayan.model.client.Address;
import dev.rayan.model.client.Client;
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

    public List<ClientByCreatedAtResponse> findClientsByCreatedAt(final ClientsByCreatedAtRequest request) {
        return createQueryFindClientsByCreatedAt(request)
                .project(ClientByCreatedAtResponse.class)
                .page(request.getPaginationRequest().getPage())
                .list();
    }

    private PanacheQuery<Client> createQueryFindClientsByCreatedAt(final ClientsByCreatedAtRequest request) {

        String query = """
                SELECT
                     firstName,
                     surname,
                     TO_CHAR(birthDate, 'dd/MM/yyyy') birthDate,
                     cpf,
                     email,
                     address
                FROM Client c
                JOIN c.credential cc
                WHERE DATE(c.createdAt) BETWEEN :startDate AND :endDate
                AND c.updatedAt IS
                """;

        query += (request.getHasUpdated()) ? " NOT NULL" : " NULL";

        final Parameters parameters = Parameters.with("startDate", request.getStartDate())
                .and("endDate", request.getEndDate());

        final Sort sort = Sort.by("firstName", request.getSortFirstName());

        return find(query, sort, parameters);
    }

    public List<ClientByCreatedAtResponse> findClientsByState(final ClientsByStateRequest request) {
//        return createQueryFindClientsByState(state)
//                .project(Number.class)

        return null;
    }

    private PanacheQuery<Client> createQueryFindClientsByState(final String state) {
        return null;
    }
}
