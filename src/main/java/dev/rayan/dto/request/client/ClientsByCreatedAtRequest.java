package dev.rayan.dto.request.client;

import dev.rayan.dto.request.page.PaginationRequest;
import io.quarkus.panache.common.Sort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public final class ClientsByCreatedAtRequest {

    @QueryParam("startDate")
    @NotNull(message = "Required start quotedAt!")
    @PastOrPresent(message = "The start quotedAt can´t be after current quotedAt!")
    LocalDate startDate;

    @QueryParam("endDate")
    @NotNull(message = "Required final quotedAt!")
    @PastOrPresent(message = "The end quotedAt can´t be after current quotedAt!")
    LocalDate endDate;

    @BeanParam
    @Valid
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
