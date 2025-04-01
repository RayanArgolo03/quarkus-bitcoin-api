package dev.rayan.services;

import dev.rayan.dto.request.transaction.TransactionByQuantityRequest;
import dev.rayan.dto.request.transaction.TransactionByTypeRequest;
import dev.rayan.dto.request.transaction.TransactionFiltersRequest;
import dev.rayan.dto.request.transaction.TransactionRequest;
import dev.rayan.dto.response.page.PageResponse;
import dev.rayan.dto.response.transaction.BitcoinResponse;
import dev.rayan.dto.response.transaction.TransactionCountResponse;
import dev.rayan.dto.response.transaction.TransactionReportResponse;
import dev.rayan.dto.response.transaction.TransactionResponse;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.mappers.TransactionMapper;
import dev.rayan.model.Client;
import dev.rayan.model.Transaction;
import dev.rayan.repositories.TransactionRepository;
import dev.rayan.utils.PaginationUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import static java.lang.String.format;

@ApplicationScoped
public final class TransactionService {

    @Inject
    TransactionRepository repository;

    @Inject
    TransactionMapper mapper;

    public TransactionResponse persist(final TransactionRequest request,
                                       final Client client,
                                       final TransactionType type,
                                       final BitcoinResponse bitcoin) {

        final Transaction transaction = mapper.requestToTransaction(request, client, type);
        repository.persist(transaction);

        return mapper.transactionToResponse(transaction, bitcoin);
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

    public PageResponse findByType(final TransactionByTypeRequest request, final Client client) {
        return PaginationUtils.paginate(
                repository.findTransactionsByType(request, client),
                null
        );
    }

    public PageResponse findByFilters(final Client client, final TransactionFiltersRequest request) {
        return PaginationUtils.paginate(
                repository.findTransactionsByFilter(client, request),
                request.getPaginationRequest()
        );
    }


    public TransactionResponse findById(final UUID transactionId, final BitcoinResponse bitcoin) {
        return repository.findByIdOptional(transactionId)
                .map(transaction -> mapper.transactionToResponse(transaction, bitcoin))
                .orElseThrow(() -> new NotFoundException("Transaction not found!"));
    }


    public PageResponse findByQuantity(final Client client, final TransactionByQuantityRequest request) {
        return PaginationUtils.paginate(
                repository.findTransactionByQuantity(client, request),
                null
        );
    }

    public TransactionCountResponse findCountByPeriod(final Client client, final TransactionReportPeriod period) {
        return repository.findTransactionCount(client, period);
    }


    public TransactionReportResponse findReport(final Client client, final TransactionReportPeriod period) {

        final TransactionCountResponse countResponse = findCountByPeriod(client, period);

        if (countResponse.count() < 1L) {
            throw new NotFoundException(format("No transactions in the %s period", period.getValue()));
        }

        return repository.findTransactionReport(client, period);
    }

    @Transactional
    @Gauge(
            name = "transactions.current.total.made",
            description = "Current total transactions made",
            absolute = true,
            unit = MetricUnits.NONE
    )
    public long findTotalMade() { return repository.count(); }

}


