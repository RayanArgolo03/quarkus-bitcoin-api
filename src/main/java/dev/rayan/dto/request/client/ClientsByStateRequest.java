package dev.rayan.dto.request.client;

import dev.rayan.dto.request.page.PaginationRequest;
import io.quarkus.panache.common.Sort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public final class ClientsByStateRequest {

    @QueryParam("startDate")
    @NotNull(message = "Required start date!")
    @PastOrPresent(message = "The start date can´t be after current date!")
    LocalDate startDate;

    @QueryParam("endDate")
    @NotNull(message = "Required end date!")
    @PastOrPresent(message = "The end date can´t be after current date!")
    LocalDate endDate;

    @Valid
    PaginationRequest paginationRequest;

    @QueryParam("sortFirstName")
    @DefaultValue("Ascending")
    Sort.Direction sortFirstName;

    @QueryParam("hasUpdated")
    @DefaultValue("false")
    Boolean hasUpdated;

}
