package dev.rayan.dto.request.transaction;

import dev.rayan.dto.request.page.PaginationRequest;
import io.quarkus.panache.common.Sort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public final class TransactionFiltersRequest {

    @NotNull(message = "Start quotedAt required!")
    @PastOrPresent(message = "The start quotedAt can´t be after current quotedAt!")
    @QueryParam("startDate")
    LocalDate startDate;

    @NotNull(message = "End quotedAt required!")
    @PastOrPresent(message = "The end quotedAt can´t be after current quotedAt!")
    @QueryParam("endDate")
    LocalDate endDate;

    @Valid
    PaginationRequest paginationRequest;

    @DefaultValue("Ascending")
    @QueryParam("sortMadeAt")
    Sort.Direction sortByMadeAtDirection;

    @DefaultValue("Ascending")
    @QueryParam("sortQuantity")
    Sort.Direction sortByQuantityDirection;

    @DefaultValue("Ascending")
    @QueryParam("sortType")
    Sort.Direction sortByType;

    @DefaultValue("0.0")
    @DecimalMin(value = "0.0", message = "Min quantity should be more than 0!")
    @QueryParam("minQuantity")
    BigDecimal minQuantity;

    @DefaultValue("10")
    @DecimalMin(value = "0.0", message = "Max quantity should be more than 0!")
    @QueryParam("maxQuantity")
    BigDecimal maxQuantity;

}
