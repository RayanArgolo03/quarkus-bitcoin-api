package dev.rayan.services;

import dev.rayan.adapters.BitcoinQuoteAdapter;
import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.dto.respose.TransactionReportResponse;
import dev.rayan.dto.respose.TransactionResponse;
import dev.rayan.dto.respose.TransactionSummaryByTypeResponse;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.exceptions.ApiException;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.mappers.TransactionMapper;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.model.client.Client;
import dev.rayan.utils.FormatterUtils;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Parameter;
import jakarta.persistence.Query;
import jakarta.ws.rs.core.UriInfo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public final class TransactionService {

    @Inject
    TransactionMapper transactionMapper;

    @Inject
    BitcoinQuoteAdapter adapter;

    public Bitcoin quoteBitcoin() {
        return adapter.quote()
                .orElseThrow(() -> new ApiException("Cannot quote bitcoin"));
    }

    public Transaction persistTransaction(final TransactionRequest request, final TransactionType type) {

        final Transaction transaction = new Transaction(request.quantity(), request.client(), type);
        Transaction.persist(transaction);

        return transaction;
    }

    public List<Transaction> findAllTransactions(final Client client) {
        return Transaction.list("client", client);
    }

    public List<TransactionSummaryByTypeResponse> findTransactionsSummaryByType(final Client client, final List<TransactionType> types) {

        //Todo remove
        client.setId(UUID.fromString("8c878e6f-ee13-4a37-a208-7510c2638944"));

        final Parameters parameters = Parameters.with("client", client);
        if (!types.isEmpty()) parameters.and("types", types);

        return Transaction.find(createQueryFindTransactionsSummaryByType(types), parameters)
                .project(TransactionSummaryByTypeResponse.class)
                .stream().toList();
    }


    private String createQueryFindTransactionsSummaryByType(final List<TransactionType> types) {

        final StringBuilder sb = new StringBuilder("SELECT \n")
                .append("CAST(type AS STRING), \n")
                .append("CAST(COUNT(*) AS STRING) transactionsMade, \n")
                .append("CAST(SUM(quantity) AS STRING) quantity, \n")
                .append("TO_CHAR(MIN(createdAt), 'YYYY-MM-DD HH24:mi') first, \n")
                .append("TO_CHAR(MAX(createdAt), 'YYYY-MM-DD HH24:mi') last, \n")
                .append("""
                        CONCAT(
                            TIMESTAMPDIFF(DAY, MIN(createdAt), MAX(createdAt)), 
                            ' day(s)'
                        ) periodBetweenFirstAndLast
                        \n""")
                .append("FROM Transaction \n")
                .append("WHERE client = :client");

        if (!types.isEmpty()) sb.append(" AND type IN (:types)");

        sb.append("\n GROUP BY type");

        return sb.toString();
    }

    public TransactionReportResponse findTransactionReport(final Client client, final TransactionReportPeriod reportPeriod) {

        //Todo remove
        client.setId(UUID.fromString("8c878e6f-ee13-4a37-a208-7510c2638944"));

        Object singleResult = createQueryFindTransactionReport()
                .setParameter("clientId", client.getId())
                .setParameter("initDate", reportPeriod.getInitDate())
                .setParameter("finalDate", reportPeriod.getFinalDate())
                .getSingleResult();

        return (TransactionReportResponse) singleResult;

    }

    private Query createQueryFindTransactionReport() {
        return Panache.getEntityManager().createNativeQuery("""
                        WITH general_attributes AS (
                            SELECT client_id, COUNT(*)::TEXT transactionsMade, TO_CHAR(MAX(created_at), 'YYYY-MM-DD HH24:mi') lastTransaction
                            FROM transactions
                            WHERE client_id = :clientId
                            GROUP BY client_id
                        ),
                                        
                        purchase_attributes AS (
                            SELECT
                                 client_id,
                                 SUM(quantity) FILTER (WHERE type = 'PURCHASE')::TEXT totalPurchased,
                                 TO_CHAR(MIN(created_at), 'YYYY-MM-DD HH24:mi') firstPurchase,
                                 TO_CHAR(MAX(created_at), 'YYYY-MM-DD HH24:mi') lastPurchase
                            FROM transactions
                            WHERE client_id = :clientId
                            AND created_at BETWEEN :initDate AND :finalDate
                            GROUP BY client_id
                        ),
                                        
                        sale_attributes AS (
                            SELECT
                                 client_id,
                                 SUM(quantity) FILTER (WHERE type = 'SALE')::TEXT totalSold,
                                 TO_CHAR(MIN(created_at), 'YYYY-MM-DD HH24:mi') firstSold,
                                 TO_CHAR(MAX(created_at), 'YYYY-MM-DD HH24:mi') lastSold
                            FROM transactions
                            WHERE client_id = :clientId
                            AND created_at BETWEEN :initDate AND :finalDate
                            GROUP BY client_id
                        )
                                        
                        SELECT transactionsMade, totalPurchased, firstPurchase, lastPurchase, totalSold, firstSold, lastSold, lastTransaction
                        FROM general_attributes
                        NATURAL JOIN purchase_attributes
                        NATURAL JOIN sale_attributes
                        """,
                TransactionReportResponse.class);
    }

    public void setBitcoinAttributesInResponse(final TransactionReportResponse response, final Bitcoin bitcoin) {

        String valuePurchased = "Sever unavailable",
                valueSold = "Sever unavailable",
                bitcoinDate = "Sever unavailable";

        if (bitcoin != null) {
            valuePurchased = FormatterUtils.formatMoney(calculateTransactionTotal(bitcoin.getLast(), response.getTotalPurchased()));
            valueSold = FormatterUtils.formatMoney(calculateTransactionTotal(bitcoin.getLast(), response.getTotalSold()));
            bitcoinDate = FormatterUtils.formatDate(bitcoin.getTime());
        }

        response.setValuePurchased(valuePurchased);
        response.setValueSold(valueSold);
        response.setBitcoinDate(bitcoinDate);
    }

    public BigDecimal calculateTransactionTotal(final BigDecimal last, final String quantity) {
        return last.multiply(new BigDecimal(quantity));
    }

    public void validateQuantity(final List<Transaction> transactions, final BigDecimal quantity) {

        final BigDecimal purchaseQuantity = sumQuantity(transactions, TransactionType.PURCHASE);
        final BigDecimal saleQuantity = sumQuantity(transactions, TransactionType.SALE);

        final BigDecimal availableQuantity = purchaseQuantity.subtract(
                saleQuantity, new MathContext(6, RoundingMode.UP)
        );

        final boolean hasQuantityAvailable = availableQuantity.compareTo(BigDecimal.ZERO) > 0;

        if (!hasQuantityAvailable) throw new BusinessException("No has bitcoins to sale!");

        if (quantity.compareTo(availableQuantity) > 0) throw new BusinessException("Cannot sell the quantity desired!");

    }

    private BigDecimal sumQuantity(final List<Transaction> transactions, final TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public TransactionResponse getMappedTransaction(final Transaction transaction, final Bitcoin bitcoin) {
        return transactionMapper.transactionInfoToTransactionResponse(transaction, bitcoin);
    }


    //Receives any ID type: UUID or Numbers implementation (int, long, float)
    public <T extends Comparable<T>> URI createUri(final UriInfo uriInfo, final T id) {
        return uriInfo.getRequestUriBuilder()
                .path("{id}")
                .resolveTemplate("id", id)
                .build();
    }


}

