package dev.rayan.repositories;

import dev.rayan.dto.request.transaction.TransactionByQuantityRequest;
import dev.rayan.dto.request.transaction.TransactionByTypeRequest;
import dev.rayan.dto.request.transaction.TransactionFiltersRequest;
import dev.rayan.dto.response.transaction.*;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.model.Client;
import dev.rayan.model.Transaction;
import dev.rayan.utils.StringToLowerUtils;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public final class TransactionRepository implements PanacheRepositoryBase<Transaction, UUID> {

    public PanacheQuery<TransactionByTypeResponse> findTransactionsByType(final TransactionByTypeRequest request, final Client client) {

        final StringBuilder query = new StringBuilder("""  
                SELECT
                     type,
                     COUNT(t) AS transactionsMade,
                     SUM(quantity) AS totalQuantity,
                     MIN(createdAt) AS  firstTransactionDate,
                     MAX(createdAt) AS lastTransactionDate,
                     CONCAT(
                        TIMESTAMPDIFF(DAY, MIN(createdAt), MAX(createdAt)),
                        ' days(s)'
                     ) AS periodBetweenFirstAndLast
                FROM
                     Transaction t
                 WHERE
                     client = :client
                """);

        final Map<String, Object> parameters = new HashMap<>(
                Map.of("client", client)
        );

        if (!request.getTypes().isEmpty()) {
            query.append("AND LOWER(CAST(type AS STRING)) IN (:types)");
            parameters.put("types", StringToLowerUtils.toLower(request.getTypes()));
        }

        query.append("GROUP BY type");

        return find(query.toString(), Sort.by("type", request.getSortType()), parameters)
                .project(TransactionByTypeResponse.class);
    }

    public PanacheQuery<TransactionByFilterResponse> findTransactionsByFilter(final Client client, final TransactionFiltersRequest request) {

        final String query = """
                SELECT type, CONCAT(quantity, ' units') AS quantity, createdAt
                FROM Transaction t
                WHERE client = :client
                AND (DATE(createdAt) BETWEEN :startDate AND :endDate)
                AND (quantity BETWEEN :minQuantity AND :maxQuantity)
                """;

        final Map<String, Object> parameters = Map.of(
                "client", client,
                "startDate", request.getDatePeriod().getStartDate(),
                "endDate", request.getDatePeriod().getEndDate(),
                "minQuantity", request.getMinQuantity(),
                "maxQuantity", request.getMaxQuantity()
        );

        final Sort sort = Sort.by("createdAt", request.getSortCreatedAt())
                .and("quantity", request.getSortQuantity())
                .and("type", request.getSortType());

        return find(query, sort, parameters)
                .page(request.getPaginationRequest().getPage())
                .project(TransactionByFilterResponse.class);
    }

    public PanacheQuery<TransactionByQuantityResponse> findTransactionByQuantity(final Client client, final TransactionByQuantityRequest request) {

        final String query = """
                SELECT t.type, t.quantity, cc.email AS clientEmail, t.createdAt
                FROM Transaction t
                JOIN t.client c
                JOIN c.credential cc
                WHERE t.quantity = :quantity
                AND t.client = :client
                """;

        final Map<String, Object> parameters = Map.of(
                "quantity", request.getQuantity(),
                "client", client
        );

        return find(query, Sort.by("t.createdAt", request.getSortCreatedAt()), parameters)
                .project(TransactionByQuantityResponse.class);
    }

    public TransactionCountResponse findTransactionCount(final Client client, final TransactionReportPeriod period) {

        final String whereFilter = """
                client = :client AND (DATE(createdAt) BETWEEN :startDate AND :endDate)
                """;

        final Map<String, Object> parameters = Map.of(
                "client", client,
                "startDate", period.getStartDate(),
                "endDate", period.getEndDate()
        );

        return new TransactionCountResponse(count(whereFilter, parameters));
    }

    public TransactionReportResponse findTransactionReport(final Client client, final TransactionReportPeriod period) {

        final String query = """
                SELECT
                    FORMAT('%s transactions', COUNT(*)) AS transactionsMade,
                    
                    SUM(t.quantity) FILTER (WHERE t.type = 'PURCHASE') AS totalPurchased,
                    TO_CHAR( MIN(t.created_at) FILTER (WHERE t.type = 'PURCHASE'), 'YYYY-MM-DD HH24:mi:ss' )AS firstPurchase,
                    TO_CHAR( MAX(t.created_at) FILTER (WHERE t.type = 'PURCHASE'), 'YYYY-MM-DD HH24:mi:ss') AS lastPurchase,
                    
                    COALESCE( SUM(t.quantity) FILTER (WHERE t.type = 'SALE'), 0) AS  totalSold,
                    COALESCE( TO_CHAR(MIN(t.created_at) FILTER (WHERE t.type = 'SALE'), 'YYYY-MM-DD HH24:mi:ss'), 'No transactions') AS firstSold,
                    COALESCE( TO_CHAR(MAX(t.created_at) FILTER (WHERE t.type = 'SALE'), 'YYYY-MM-DD HH24:mi:ss'), 'No transactions') AS lastSold,
                    
                    TO_CHAR(MAX(t.created_at), 'YYYY-MM-DD HH24:mi:ss') AS  lastTransaction
                FROM transactions t
                WHERE credential_id = :id AND (DATE(created_at) BETWEEN :startDate AND :endDate)
                """;

        return (TransactionReportResponse)
                getEntityManager().createNativeQuery(query, TransactionReportResponse.class)
                        .setParameter("id", client.getId())
                        .setParameter("startDate", period.getStartDate())
                        .setParameter("endDate", period.getEndDate())
                        .getSingleResult();
    }

}
