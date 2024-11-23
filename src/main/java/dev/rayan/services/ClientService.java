package dev.rayan.services;

import dev.rayan.adapters.BitcoinQuoteAdapter;
import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.dto.respose.TransactionResponse;
import dev.rayan.exceptions.ApiException;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.mappers.TransactionMapper;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.*;
import jakarta.ws.rs.WebApplicationException;

import java.util.Set;

import static jakarta.ws.rs.core.Response.Status.*;

@ApplicationScoped
public final class ClientService {

    @Inject
    Validator validator;

    @Inject
    TransactionMapper transactionMapper;

    @Inject
    BitcoinQuoteAdapter adapter;

    public Transaction createTransaction(final TransactionRequest request) {

        final Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) throw new BusinessException(violations);

        return new Transaction(request.quantity(), request.client());
    }

    public Bitcoin quoteBitcoin() {
        return adapter.quote()
                .orElseThrow(() -> new ApiException("Server unavailable"));
    }

    public TransactionResponse getMappedTransaction(final Transaction transaction, final Bitcoin bitcoin) {
        return transactionMapper.transactionInfoToTransactionResponse(transaction, bitcoin);
    }

    public TransactionResponse getMappedTransaction(final Transaction transaction, final String message) {
        return transactionMapper.transactionInfoToTransactionResponse(transaction, message);
    }
}
