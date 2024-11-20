package dev.rayan.dto.request;

import dev.rayan.model.client.Client;

public record TransactionRequestDTO(float bitcoinQuantity, Client client) { }
