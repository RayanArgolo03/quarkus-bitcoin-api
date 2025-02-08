package dev.rayan.dto.request.client;

import dev.rayan.dto.request.page.PaginationRequest;
import io.quarkus.panache.common.Sort;
import jakarta.validation.Valid;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;

@Getter
public final class ClientsByAddressFilterRequest {

    @Valid
    AddressFilterRequest addressFilterRequest;

    @Valid
    PaginationRequest paginationRequest;

    @QueryParam("sortFirstName")
    @DefaultValue("Ascending")
    Sort.Direction sortFirstName;
}
