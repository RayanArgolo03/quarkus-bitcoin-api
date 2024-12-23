package dev.rayan.dto.request;

import dev.rayan.model.client.Client;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(
        @DecimalMin(value = "0.000001", inclusive = false, message = "Quantity must be greater than 0.000001!")
        @NotNull(message = "Quantity required!") BigDecimal quantity,

        @NotNull(message = "Client required!") Client client) {}
