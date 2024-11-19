package dev.rayan.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.rayan.model.client.Client;

public record TransactionCreatedDTO(float bitcoinQuantity, Client client) {
}
