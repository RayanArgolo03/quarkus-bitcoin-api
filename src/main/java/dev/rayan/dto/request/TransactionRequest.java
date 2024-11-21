package dev.rayan.dto.request;

import dev.rayan.model.client.Client;

public record TransactionRequest(float bitcoinQuantity, Client client) { }
