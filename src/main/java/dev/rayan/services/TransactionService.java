package dev.rayan.services;

import dev.rayan.adapter.BitcoinQuoteAdapter;
import dev.rayan.dto.request.transaction.TransactionByTypeRequest;
import dev.rayan.dto.request.transaction.TransactionFiltersRequest;
import dev.rayan.dto.request.transaction.TransactionRequest;
import dev.rayan.dto.response.bitcoin.BitcoinResponse;
import dev.rayan.dto.response.page.PageResponse;
import dev.rayan.dto.response.transaction.TransactionReportResponse;
import dev.rayan.dto.response.transaction.TransactionResponse;
import dev.rayan.dto.response.transaction.TransactionByFilterResponse;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.exceptions.ApiException;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.mappers.TransactionMapper;
import dev.rayan.model.Client;
import dev.rayan.model.Transaction;
import dev.rayan.repositories.TransactionRepository;
import dev.rayan.utils.PaginationUtils;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

@ApplicationScoped
public final class TransactionService {

    @Inject
    TransactionRepository repository;

    @Inject
    TransactionMapper mapper;

    @Inject
    BitcoinQuoteAdapter adapter;

    public TransactionResponse persist(final TransactionRequest request,
                                       final Client client,
                                       final TransactionType type,
                                       final BitcoinResponse bitcoin) {

        final Transaction transaction = mapper.requestToTransaction(request, client, type);
        repository.persist(transaction);

        return mapper.transactionToResponse(transaction, bitcoin);
    }

    public BitcoinResponse quoteBitcoin() {
        return adapter.quote()
                .orElseThrow(() -> new ApiException("The server was unable to complete your request, contact @rayan_argolo"));
    }

    public void validateTransaction(final Client client, final BigDecimal quantity) {

        final List<Transaction> transactions = repository.find("client", client)
                .list();

        if (transactions.isEmpty()) throw new NotAuthorizedException("No has transactions!", UNAUTHORIZED);

        final BigDecimal purchaseQuantity = sumQuantity(transactions, TransactionType.PURCHASE);
        final BigDecimal saleQuantity = sumQuantity(transactions, TransactionType.SALE);

        final BigDecimal availableQuantity = purchaseQuantity.subtract(
                saleQuantity, new MathContext(6, RoundingMode.UP)
        );

        final boolean hasAvailableQuantity = availableQuantity.compareTo(BigDecimal.ZERO) > 0;
        if (!hasAvailableQuantity) throw new BusinessException("No has bitcoins to sale!");

        final boolean isValidQuantity = availableQuantity.compareTo(quantity) > -1;
        if (!isValidQuantity) throw new BusinessException("Cannot sell the quantity desired!");

    }

    private BigDecimal sumQuantity(final List<Transaction> transactions, final TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public PageResponse findTransactionsSummaryByType(final TransactionByTypeRequest request, final Client client) {
        return PaginationUtils.paginate(
                repository.findTransactionsSummaryByType(request, client),
                null
        );
    }


    public Transaction findTransactionById(final UUID id) {
//        final Optional<Transaction> optional = Transaction.findByIdOptional(id);
//        return optional.orElseThrow(() -> new NotFoundException("Transaction not found!"));
        return null;
    }


    public Transaction findTransactionByQuantity(final UUID clientId, final BigDecimal quantity, final Sort.Direction sortCreatedAt) {

//        final Optional<Transaction> optional = Transaction.find(
//                "client.id = ?1 AND totalQuantity = ?2",
//                Sort.by("quotedAt", sortCreatedAt),
//                clientId, totalQuantity
//        ).firstResultOptional();
//
//        return optional.orElseThrow(() -> new NotFoundException("Transaction not found!"));
        return null;
    }

    public List<TransactionByFilterResponse> findTransactionSummaryByFilters(final UUID clientId, final TransactionFiltersRequest request) {

        final Parameters parameters = Parameters.with("clientId", clientId)
                .and("startDate", request.getStartDate())
                .and("endDate", request.getEndDate())
                .and("minQuantity", request.getMinQuantity())
                .and("maxQuantity", request.getMaxQuantity());

        final List<TransactionByFilterResponse> transactions = createQueryFindTransactionSummaryByFilters(request, parameters)
                .project(TransactionByFilterResponse.class)
                .page(request.getPaginationRequest().getPage())
                .list();

        if (transactions.isEmpty()) throw new NotFoundException("Transactions not found!");
        return transactions;
    }

    private PanacheQuery<Transaction> createQueryFindTransactionSummaryByFilters(final TransactionFiltersRequest request, final Parameters parameters) {

        final Sort sort = Sort.by("quotedAt", request.getSortByMadeAtDirection())
                .and("type", request.getSortByType())
                .and("totalQuantity", request.getSortByQuantityDirection());

//        return Transaction.find("""
//                SELECT TO_CHAR(quotedAt, 'YYYY-MM-DD') madeAt, CAST(totalQuantity AS STRING), CAST(type AS STRING)
//                FROM Transaction
//                WHERE client.id = :clientId
//                AND DATE(quotedAt) BETWEEN :startDate AND :endDate
//                AND totalQuantity BETWEEN :minQuantity AND :maxQuantity
//                """, sort, parameters);
        return null;
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

//        return Transaction.find("""
//                SELECT
//                    CAST(COUNT(*) AS STRING) transactionsMade,
//
//                    COALESCE( CAST(SUM(CASE WHEN type = 'PURCHASE' THEN totalQuantity END) AS STRING), '0') totalPurchased,
//                    COALESCE( TO_CHAR(MIN(CASE WHEN type = 'PURCHASE' THEN quotedAt END), 'YYYY-MM-DD HH24:mi'), 'No transactions') firstPurchase,
//                    COALESCE( TO_CHAR(MAX(CASE WHEN type = 'PURCHASE' THEN quotedAt END), 'YYYY-MM-DD HH24:mi'), 'No transactions') lastPurchase,
//
//                    COALESCE( CAST(SUM(CASE WHEN type = 'SALE' THEN totalQuantity END) AS STRING), '0') totalSold,
//                    COALESCE( TO_CHAR(MIN(CASE WHEN type = 'SALE' THEN quotedAt END), 'YYYY-MM-DD HH24:mi'), 'No transactions') firstSold,
//                    COALESCE( TO_CHAR(MAX(CASE WHEN type = 'SALE' THEN quotedAt END), 'YYYY-MM-DD HH24:mi'), 'No transactions') lastSold,
//
//                    TO_CHAR(MAX(quotedAt), 'YYYY-MM-DD HH24:mi') lastTransaction
//                FROM Transaction
//                WHERE client.id = :clientId
//                AND DATE(quotedAt) BETWEEN :startDate AND :endDate
//                GROUP BY client
//                """, parameters);
        return null;
    }

    public void setBitcoinAttributesInResponse(final TransactionReportResponse reportResponse, final BitcoinResponse bitcoinResponse) {

        String value = "Server unavailable", valuePurchased = value, valueSold = value, bitcoinDate = value;

        if (bitcoinResponse != null) {

            final NumberFormat formatter = NumberFormat.getCurrencyInstance();

//            valuePurchased = formatter.format(
//                    calculateTransactionTotal(bitcoinResponse.getLast(), reportResponse.getTotalPurchased())
//            )


//            valueSold = formatter.format(
//                    calculateTransactionTotal(bitcoinResponse.getLast(), reportResponse.getTotalSold())
//            );


            bitcoinDate = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT)
                    .format(null);
        }

        reportResponse.setValuePurchased(valuePurchased);
        reportResponse.setValueSold(valueSold);
        reportResponse.setBitcoinDate(bitcoinDate);
    }


}


