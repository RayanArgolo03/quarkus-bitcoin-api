package dev.rayan.dto.request;

import dev.rayan.model.client.Client;

public record TransactionRequest(float quantity, Client client) { }
