package dev.rayan.dto.request;

import dev.rayan.model.client.Client;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(
        @DecimalMin(value = "0.00001", message = "Minimum quantity required: 0.00001") float quantity,

        //Todo cliente precisa estar logado, vê rules
        @NotNull
        Client client) {
}
