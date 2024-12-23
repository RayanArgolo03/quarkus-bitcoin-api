package dev.rayan.dto.request;

import io.quarkus.panache.common.Sort;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public final class TransactionByQuantityRequest {

    @QueryParam("quantity")
    @DecimalMin(value = "0", inclusive = false, message = "Quantity must be greater than 0!!")
    @NotNull(message = "Quantity required!")
    BigDecimal quantity;

    @QueryParam("sortCreatedAt")
    @DefaultValue("Ascending")
    Sort.Direction sortCreatedAtDirection;

}
