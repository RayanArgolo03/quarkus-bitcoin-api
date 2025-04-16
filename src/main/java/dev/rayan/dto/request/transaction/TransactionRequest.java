package dev.rayan.dto.request.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(

        @NotNull(message = "Quantity required!")
        @DecimalMin(
                value = "0.000001",
                inclusive = true,
                message = "Quantity must be greater than 0.000001!"
        )
        BigDecimal quantity) {


}
