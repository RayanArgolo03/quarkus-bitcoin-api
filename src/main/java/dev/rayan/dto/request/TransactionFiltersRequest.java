package dev.rayan.dto.request;

import io.quarkus.panache.common.Sort;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionFiltersRequest {

    @NotNull(message = "Start date required!")
    @PastOrPresent(message = "The start date can´t be after current date!")
    @QueryParam("startDate")
    LocalDate startDate;

    @NotNull(message = "End date required!")
    @PastOrPresent(message = "The end date can´t be after current date!")
    @QueryParam("endDate")
    LocalDate endDate;

    @DefaultValue("0")
    @Min(value = 0, message = "Page index can´t be less than " + 0)
    @QueryParam("pageIndex")
    Integer pageIndex;

    @DefaultValue("10")
    @Min(value = 0, message = "Page size can´t be less than " + 0)
    @QueryParam("pageSize")
    Integer pageSize;

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
