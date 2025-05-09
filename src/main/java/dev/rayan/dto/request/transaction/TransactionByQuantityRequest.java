package dev.rayan.dto.request.transaction;

import dev.rayan.dto.request.page.PaginationRequest;
import io.quarkus.panache.common.Sort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

import java.math.BigDecimal;

public record TransactionByQuantityRequest(

        @Valid
        @BeanParam
        PaginationRequest paginationRequest,

        @QueryParam("quantity")
        @DecimalMin(value = "0", inclusive = false, message = "Quantity must be greater than 0!!")
        @NotNull(message = "Quantity required!")
        BigDecimal quantity,

        @QueryParam("sortCreatedAt")
        @DefaultValue("Descending")
        Sort.Direction sortCreatedAt
) {
}
