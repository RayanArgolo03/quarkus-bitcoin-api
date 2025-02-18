package dev.rayan.repositories;

import dev.rayan.dto.request.transaction.TransactionByTypeRequest;
import dev.rayan.dto.response.transaction.TransactionByTypeResponse;
import dev.rayan.model.Client;
import dev.rayan.model.Transaction;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public final class TransactionRepository implements PanacheRepositoryBase<Transaction, UUID> {

    public PanacheQuery<TransactionByTypeResponse> findTransactionsSummaryByType(final TransactionByTypeRequest request, final Client client) {

        final StringBuilder query = new StringBuilder("""  
                SELECT
                     type,
                     COUNT(t) transactionsMade,
                     SUM(quantity) totalQuantity,
                     MIN(createdAt) firstTransactionDate,
                     MAX(createdAt) lastTransactionDate,
                     CONCAT(
                        TIMESTAMPDIFF(DAY, MIN(createdAt), MAX(createdAt)),
                        ' days(s)'
                     ) periodBetweenFirstAndLast
                FROM
                     Transaction t
                 WHERE
                     client = :client
                """);

        final Parameters parameters = Parameters.with("client", client);

        if (!request.getTypes().isEmpty()) {
            query.append("AND LOWER(CAST(type AS STRING)) IN (:types)");
            parameters.and("types", stringListToLowerCase(request.getTypes()));
        }

        query.append("GROUP BY type");

        return find(query.toString(), Sort.by("type", request.getSortType()), parameters)
                .project(TransactionByTypeResponse.class);
    }

    private List<String> stringListToLowerCase(final List<String> elements) {
        return elements.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

}
