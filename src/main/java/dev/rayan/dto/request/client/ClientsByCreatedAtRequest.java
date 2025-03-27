package dev.rayan.dto.request.client;

import dev.rayan.dto.request.page.DatePeriodRequest;
import dev.rayan.dto.request.page.PaginationRequest;
import io.quarkus.panache.common.Sort;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public final class ClientsByCreatedAtRequest {

    @Valid
    @BeanParam
    DatePeriodRequest datePeriod;

    @Valid
    @BeanParam
    PaginationRequest pagination;

    @QueryParam("sortFirstName")
    @DefaultValue("Descending")
    Sort.Direction sortFirstName;

    @QueryParam("hasUpdated")
    @DefaultValue("false")
    @Getter(AccessLevel.NONE)
    boolean hasUpdated;

    public boolean hasUpdated() {
        return hasUpdated;
    }
}
