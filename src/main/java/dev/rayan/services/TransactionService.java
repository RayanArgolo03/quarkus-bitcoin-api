package dev.rayan.services;

import dev.rayan.adapters.BitcoinQuoteAdapter;
import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.dto.respose.TransactionResponse;
import dev.rayan.dto.respose.TransactionSummaryByTypeResponse;
import dev.rayan.enums.TransactionType;
import dev.rayan.exceptions.ApiException;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.mappers.TransactionMapper;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.model.client.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
                .orElseThrow(() -> new ApiException("Server unavailable"));
    }

    public Transaction persistTransaction(final TransactionRequest request, final TransactionType type) {

        final Transaction transaction = new Transaction(request.quantity(), request.client(), type);
        Transaction.persist(transaction);

        return transaction;
    }

    public List<Transaction> findAllTransactions(final Client client) {
        return Transaction.list("client", client);
    }

    public TransactionSummaryByTypeResponse findTransactionsByType(final Client client, final TransactionType type) {

        client.setId(UUID.fromString("8c878e6f-ee13-4a37-a208-7510c2638944"));

        final String query = """
                SELECT
                    CAST(COUNT(*) AS STRING) transactionsMade,
                    CAST(SUM(quantity) AS STRING) quantity
                FROM Transaction
                WHERE client = ?1 AND type = ?2
                """;

        return Transaction.find(query, client, type)
                .project(TransactionSummaryByTypeResponse.class)
                .firstResult();
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

