package dev.rayan.services;

import dev.rayan.adapters.BitcoinQuoteAdapter;
import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.dto.respose.TransactionResponse;
import dev.rayan.enums.TransactionType;
import dev.rayan.exceptions.ApiException;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.mappers.TransactionMapper;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public final class ClientService {

    @Inject
    Validator validator;

    @Inject
    TransactionMapper transactionMapper;

    @Inject
    BitcoinQuoteAdapter adapter;

    public Transaction persistTransaction(final TransactionRequest request, final TransactionType type) {

        validateRequest(request);

        final Transaction transaction = new Transaction(request.quantity(), request.client(), type);
        Transaction.persist(transaction);

        return transaction;
    }

    public Bitcoin quoteBitcoin() {
        return adapter.quote()
                .orElseThrow(() -> new ApiException("Server unavailable"));
    }

    public List<Transaction> findAll(final TransactionRequest request) {
        validateRequest(request);
        return Transaction.list("client.id", request.client().getId());
    }

    public void validateQuantity(final List<Transaction> transactions, final float quantity) {

        float purchaseQuantity = sumQuantity(transactions, TransactionType.BUY);
        float saleQuantity = sumQuantity(transactions, TransactionType.SALE);

        boolean hasPurchase = purchaseQuantity > 0.0f;
        boolean hasQuantityAvailable = hasPurchase && purchaseQuantity != saleQuantity;

        if (!hasPurchase || !hasQuantityAvailable) throw new BusinessException("No has bitcoins to sale!");

        if (quantity > purchaseQuantity) throw new BusinessException("Cannot sell the quantity desired!");

    }

    private float sumQuantity(final List<Transaction> transactions, final TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getQuantity)
                .reduce(0.0f, Float::sum);
    }

    public TransactionResponse getMappedTransaction(final Transaction transaction, final Bitcoin bitcoin) {
        return transactionMapper.transactionInfoToTransactionResponse(transaction, bitcoin);
    }


    public <T> void validateRequest(final T request) {
        final Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) throw new BusinessException(violations);
    }

}

