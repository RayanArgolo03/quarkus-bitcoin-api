package dev.rayan.repositories;

import dev.rayan.dto.request.client.AddressFilterRequest;
import dev.rayan.dto.request.client.ClientsByAddressFilterRequest;
import dev.rayan.dto.request.client.ClientsByCreatedAtRequest;
import dev.rayan.dto.request.client.UpdateClientRequest;
import dev.rayan.dto.response.client.FoundClientResponse;
import dev.rayan.model.Address;
import dev.rayan.model.Client;
import dev.rayan.utils.StringToLowerUtils;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public final class ClientRepository implements PanacheRepositoryBase<Client, UUID> {

    private static final String FIND_CLIENT_QUERY = """
            SELECT c.firstName, c.surname, c.birthDate, c.cpf, cc.email, c.address
            FROM Client c
            JOIN c.credential cc
            """;

    public Optional<String> findCpf(final String cpf) {
        return find("#Client.findCpf", cpf)
                .project(String.class)
                .singleResultOptional();
    }


    public PanacheQuery<FoundClientResponse> findClientsByCreatedAt(final ClientsByCreatedAtRequest request) {

        final String hasUpdatedParam = request.hasUpdated() ? "IS NOT NULL" : "IS NULL";

        final StringBuilder baseQuery = new StringBuilder(FIND_CLIENT_QUERY)
                .append("WHERE DATE(c.createdAt) BETWEEN :startDate AND :endDate AND c.updatedAt")
                .append(hasUpdatedParam);

        final Map<String, Object> parameters = Map.of(
                "startDate", request.getDatePeriod().getStartDate(),
                "endDate", request.getDatePeriod().getEndDate()
        );

        final Sort sort = Sort.by("firstName", request.getSortFirstName());

        return find(baseQuery.toString(), sort, parameters)
                .page(request.getPagination().getPage())
                .project(FoundClientResponse.class);
    }


    public PanacheQuery<FoundClientResponse> findClientsByAddressFilter(final ClientsByAddressFilterRequest request) {

        final Sort sortCreatedAt = Sort.by("c.createdAt", request.getSortCreatedAt());
        final Class<FoundClientResponse> responseClass = FoundClientResponse.class;
        final Page page = request.getPagination().getPage();

        final AddressFilterRequest addressFilter = request.getAddressFilter();
        final Map<String, Object> parameters = new HashMap<>();

        addParamIfNotEmpty(parameters, "state", addressFilter.states());
        addParamIfNotEmpty(parameters, "street", addressFilter.streets());
        addParamIfNotEmpty(parameters, "neighbourhood", addressFilter.neighbourhoods());

        if (parameters.isEmpty()) {
            return find(FIND_CLIENT_QUERY, sortCreatedAt)
                    .page(page)
                    .project(responseClass);
        }

        final StringBuilder baseQuery = new StringBuilder(FIND_CLIENT_QUERY)
                .append("WHERE")
                .append(createQueryFilter(parameters));

        return find(baseQuery.toString(), sortCreatedAt, parameters)
                .page(page)
                .project(responseClass);
    }

    private void addParamIfNotEmpty(final Map<String, Object> parameters, final String attribute, final Set<String> inputs) {
        if (!inputs.isEmpty()) {
            parameters.put(attribute, StringToLowerUtils.toLower(inputs));
        }
    }

    private String createQueryFilter(final Map<String, Object> parameters) {

        final String attributeToLower = "LOWER(address.",
                inClause = ") IN (:",
                endQuery = ")",
                orClause = " OR ";

        return parameters.keySet().stream()
                .map(attribute -> attributeToLower + attribute + inClause + attribute + endQuery)
                .collect(Collectors.joining(orClause));
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
