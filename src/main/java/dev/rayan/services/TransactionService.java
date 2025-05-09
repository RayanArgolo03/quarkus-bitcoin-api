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
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Unremovable
@ApplicationScoped
public class TransactionService {

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

        final List<Transaction> transactions = repository.findAllTransactions(client);

        if (transactions.isEmpty()) {
            throw new ForbiddenException("You don´t have transactions made!");
        }

        final BigDecimal purchaseSum = sumQuantity(TransactionType.PURCHASE, transactions);
        final BigDecimal saleSum = sumQuantity(TransactionType.SALE, transactions);

        final MathContext calcPrecision = new MathContext(6, RoundingMode.HALF_UP);
        final BigDecimal availableQuantity = purchaseSum.subtract(saleSum, calcPrecision);

        final boolean hasAvailableQuantity = availableQuantity.signum() > 0;
        if (!hasAvailableQuantity) {
            throw new BusinessException("No has bitcoins to sale!");
        }

        final boolean isValidQuantity = availableQuantity.compareTo(quantity) > -1;
        if (!isValidQuantity) {
            final String messageFormat = "Quantity desired (%s) is greater than the available quantity (%s)!";
            throw new BusinessException(format(messageFormat, quantity, availableQuantity));
        }
    }

    private BigDecimal sumQuantity(final TransactionType type, final List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == type)
                .map(Transaction::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public PageResponse findByTypes(final TransactionByTypeRequest request,
                                    final Client client,
                                    final List<TransactionType> transactionTypes) {
        return PaginationUtils.paginate(
                repository.findTransactionsByTypes(request, client, transactionTypes),
                null
        );
    }

    public PageResponse findByFilters(final TransactionFiltersRequest request, final Client client) {
        return PaginationUtils.paginate(
                repository.findTransactionsByFilters(request, client),
                request.paginationRequest()
        );
    }

    public TransactionResponse findById(final String transactionId, final BitcoinResponse bitcoin) {
        return repository.findByIdOptional(UUID.fromString(transactionId))
                .map(transaction -> mapper.transactionToResponse(transaction, bitcoin))
                .orElseThrow(() -> new NotFoundException("Transaction not found!"));
    }


    public PageResponse findByQuantity(final TransactionByQuantityRequest request, final Client client) {
        return PaginationUtils.paginate(
                repository.findTransactionByQuantity(request, client),
                request.paginationRequest()
        );
    }

    public TransactionCountResponse findCountByPeriod(final TransactionReportPeriod period, final Client client) {
        return repository.findTransactionCount(period, client);
    }

    public TransactionReportResponse findReport(final TransactionReportPeriod period, final Client client) {

        boolean hasTransactions = findCountByPeriod(period, client).transactionsInPeriod() > 0L;

        if (!hasTransactions) {
            final String messageFormat = "No has transactions in the %s period";
            throw new ForbiddenException(format(messageFormat, period.getValue()));
        }

        return repository.findTransactionReport(period, client);
    }

    /*@Gauge(
            name = "transactions.current.total.made",
            description = "Current total transactions made",
            absolute = true,
            unit = MetricUnits.NONE
    )
    public long findTotalMade() {
        log.info("Collecting Gauge metric");
        return repository.transactionsInPeriod();
    }*/

}


