package dev.rayan.services;

import dev.rayan.adapters.BitcoinQuoteAdapter;
import dev.rayan.dto.request.TransactionRequestDTO;
import dev.rayan.dto.respose.TransactionResponseDTO;
import dev.rayan.mappers.TransactionMapper;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import static jakarta.ws.rs.core.Response.Status.*;

@ApplicationScoped
public final class ClientService {

    @Inject
    TransactionMapper transactionMapper;

    @Inject
    BitcoinQuoteAdapter adapter;

    public Transaction createTransaction(final TransactionRequestDTO dto) {
        //Todo validate aqui com Hibernate validator e seguindo outros projetos - Estoura exception?
        return new Transaction(dto.bitcoinQuantity(), dto.client());
    }

    public Bitcoin quote() {
        return adapter.quote()
                .orElseThrow(() -> new WebApplicationException("", SERVICE_UNAVAILABLE));
    }

    public TransactionResponseDTO getMappedTransaction(final Transaction transaction, final Bitcoin bitcoin) {
        return transactionMapper.transactionInfoToTransactionResponseDTO(transaction, bitcoin);
    }

}
