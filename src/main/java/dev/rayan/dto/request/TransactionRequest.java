package dev.rayan.dto.request;

import dev.rayan.model.client.Client;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(
        @DecimalMin(value = "0.000001", inclusive = false, message = "Quantity must be greater than 0.000001!") @NotNull(message = "Empty quantity!") BigDecimal quantity,
        //Todo cliente precisa estar logado, vê rules
        @NotNull(message = "Client can´t be null") Client client) {
}
