package dev.rayan.dto.request.transaction;

import dev.rayan.dto.request.page.DatePeriodRequest;
import dev.rayan.dto.request.page.PaginationRequest;
import io.quarkus.panache.common.Sort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionFiltersRequest(

        @Valid
        @BeanParam
        DatePeriodRequest datePeriod,

        @Valid
        @BeanParam
        PaginationRequest paginationRequest,

        @DefaultValue("0.00001")
        @Positive(message = "Min quantity should be more than 0!")
        @QueryParam("minQuantity")
        BigDecimal minQuantity,

        @DefaultValue("10")
        @DecimalMin(value = "0", message = "Max quantity should be more than 0!")
        @QueryParam("maxQuantity")
        BigDecimal maxQuantity,

        @DefaultValue("Ascending")
        @QueryParam("sortMadeAt")
        Sort.Direction sortCreatedAt,

        @DefaultValue("Ascending")
        @QueryParam("sortQuantity")
        Sort.Direction sortQuantity,

        @DefaultValue("Ascending")
        @QueryParam("sortType")
        Sort.Direction sortType

) {
}
