package dev.rayan.services;

import dev.rayan.adapter.BitcoinQuoteAdapter;
import dev.rayan.dto.request.TransactionFiltersRequest;
import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.dto.respose.TransactionReportResponse;
import dev.rayan.dto.respose.TransactionResponse;
import dev.rayan.dto.respose.TransactionSummaryByFiltersResponse;
import dev.rayan.dto.respose.TransactionSummaryByTypeResponse;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.mappers.TransactionMapper;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.model.client.Client;
import dev.rayan.utils.FormatterUtils;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public final class TransactionService {

    @Inject
    TransactionMapper mapper;

    @Inject
    BitcoinQuoteAdapter adapter;

    public Bitcoin quoteBitcoin() {
        return adapter.quote()
                .orElse(null);
    }

    public Transaction persist(final TransactionRequest request, final TransactionType type) {

        final Transaction transaction = new Transaction(request.quantity(), request.client(), type);
        Transaction.persist(transaction);

        return transaction;
    }

    public List<Transaction> findAllTransactions(final Client client) {
        return Transaction.list("client", client);
    }

    public Transaction findTransactionById(final UUID id) {
        final Optional<Transaction> optional = Transaction.findByIdOptional(id);
        return optional.orElseThrow(() -> new NotFoundException("Transaction not found!"));
    }


    public Transaction findTransactionByQuantity(final UUID clientId, final BigDecimal quantity, final Sort.Direction sortCreatedAt) {

        final Optional<Transaction> optional = Transaction.find(
                "client.id = ?1 AND quantity = ?2",
                Sort.by("createdAt", sortCreatedAt),
                clientId, quantity
        ).firstResultOptional();

        return optional.orElseThrow(() -> new NotFoundException("Transaction not found!"));
    }


    public List<TransactionSummaryByTypeResponse> findTransactionsSummaryByType(final UUID clientId, final List<TransactionType> types) {

        final Parameters parameters = Parameters.with("clientId", clientId);
        if (!types.isEmpty()) parameters.and("types", types);

        final List<TransactionSummaryByTypeResponse> transactions = createQueryFindTransactionsSummaryByType(types, parameters)
                .project(TransactionSummaryByTypeResponse.class)
                .list();

        if (transactions.isEmpty()) throw new NotFoundException("Transactions not found!");
        return transactions;
    }


    private PanacheQuery<Transaction> createQueryFindTransactionsSummaryByType(final List<TransactionType> types, final Parameters parameters) {

        String query = """
                      SELECT
                          CAST(type AS STRING),
                          CAST(COUNT(*) AS STRING) transactionsMade,
                          CAST(SUM(quantity) AS STRING) quantity,
                          TO_CHAR(MIN(createdAt), 'YYYY-MM-DD HH24:mi') first,
                          TO_CHAR(MAX(createdAt), 'YYYY-MM-DD HH24:mi') last,
                          CONCAT(
                              TIMESTAMPDIFF(DAY, MIN(createdAt), MAX(createdAt) ),
                              ' day(s)'
                          ) periodBetweenFirstAndLast
                       FROM Transaction
                       WHERE client.id = :clientId
                """;

        if (!types.isEmpty()) query += " AND type IN (:types)";
        query += " GROUP BY type";

        return Transaction.find(query, parameters);
    }

    public List<TransactionSummaryByFiltersResponse> findTransactionSummaryByFilters(final UUID clientId, final TransactionFiltersRequest request) {

        final Parameters parameters = Parameters.with("clientId", clientId)
                .and("startDate", request.getStartDate())
                .and("endDate", request.getEndDate())
                .and("minQuantity", request.getMinQuantity())
                .and("maxQuantity", request.getMaxQuantity());

        final List<TransactionSummaryByFiltersResponse> transactions = createQueryFindTransactionSummaryByFilters(request, parameters)
                .project(TransactionSummaryByFiltersResponse.class)
                .page(request.getPageIndex(), request.getPageSize())
                .list();

        if (transactions.isEmpty()) throw new NotFoundException("Transactions not found!");
        return transactions;
    }

    private PanacheQuery<Transaction> createQueryFindTransactionSummaryByFilters(final TransactionFiltersRequest request, final Parameters parameters) {

        final Sort sort = Sort.by("createdAt", request.getSortByMadeAtDirection())
                .and("type", request.getSortByType())
                .and("quantity", request.getSortByQuantityDirection());

        return Transaction.find("""
                SELECT TO_CHAR(createdAt, 'YYYY-MM-DD') madeAt, CAST(quantity AS STRING), CAST(type AS STRING)
                FROM Transaction
                WHERE client.id = :clientId
                AND DATE(createdAt) BETWEEN :startDate AND :endDate
                AND quantity BETWEEN :minQuantity AND :maxQuantity
                """, sort, parameters);
    }

    public TransactionReportResponse findTransactionReport(final UUID clientId, final TransactionReportPeriod period) {

        final Parameters parameters = Parameters.with("clientId", clientId)
                .and("startDate", period.getStartDate())
                .and("endDate", period.getEndDate());

        final Optional<TransactionReportResponse> optional = createQueryFindTransactionReport(parameters)
                .project(TransactionReportResponse.class)
                .firstResultOptional();

        return optional
                .orElseThrow(() -> new NotFoundException("You haven´t made transactions yet!"));
    }

    private PanacheQuery<Transaction> createQueryFindTransactionReport(final Parameters parameters) {

        return Transaction.find("""                
                SELECT
                    CAST(COUNT(*) AS STRING) transactionsMade,
                   
                    COALESCE( CAST(SUM(CASE WHEN type = 'PURCHASE' THEN quantity END) AS STRING), '0') totalPurchased,
                    COALESCE( TO_CHAR(MIN(CASE WHEN type = 'PURCHASE' THEN createdAt END), 'YYYY-MM-DD HH24:mi'), 'No transactions') firstPurchase,
                    COALESCE( TO_CHAR(MAX(CASE WHEN type = 'PURCHASE' THEN createdAt END), 'YYYY-MM-DD HH24:mi'), 'No transactions') lastPurchase,
                    
                    COALESCE( CAST(SUM(CASE WHEN type = 'SALE' THEN quantity END) AS STRING), '0') totalSold,
                    COALESCE( TO_CHAR(MIN(CASE WHEN type = 'SALE' THEN createdAt END), 'YYYY-MM-DD HH24:mi'), 'No transactions') firstSold,
                    COALESCE( TO_CHAR(MAX(CASE WHEN type = 'SALE' THEN createdAt END), 'YYYY-MM-DD HH24:mi'), 'No transactions') lastSold,
                    
                    TO_CHAR(MAX(createdAt), 'YYYY-MM-DD HH24:mi') lastTransaction
                FROM Transaction
                WHERE client.id = :clientId
                AND DATE(createdAt) BETWEEN :startDate AND :endDate
                GROUP BY client
                """, parameters);
    }

    public void setBitcoinAttributesInResponse(final TransactionReportResponse reportResponse, final Bitcoin bitcoin) {

        String value = "Server unavailable", valuePurchased = value, valueSold = value, bitcoinDate = value;

        if (bitcoin != null) {

            valuePurchased = FormatterUtils.formatMoney(
                    calculateTransactionTotal(bitcoin.getLast(), reportResponse.getTotalPurchased())
            );

            valueSold = FormatterUtils.formatMoney(
                    calculateTransactionTotal(bitcoin.getLast(), reportResponse.getTotalSold())
            );

            bitcoinDate = FormatterUtils.formatDate(bitcoin.getTime());
        }

        reportResponse.setValuePurchased(valuePurchased);
        reportResponse.setValueSold(valueSold);
        reportResponse.setBitcoinDate(bitcoinDate);
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
        return mapper.transactionInfoToTransactionResponse(transaction, bitcoin);
    }


}


