package dev.rayan.services;

import dev.rayan.dto.request.TransactionCreatedDTO;
import dev.rayan.model.bitcoin.Transaction;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public final class ClientService {

    public Transaction createTransaction(final TransactionCreatedDTO dto) {
       //Todo validate aqui com Hibernate validator e seguindo outros projetos
        return new Transaction(dto.bitcoinQuantity(), dto.client());
    }

}
