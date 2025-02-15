package dev.rayan.dto.request.client;

import dev.rayan.dto.request.page.PaginationRequest;
import io.quarkus.panache.common.Sort;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;

@Getter
public final class ClientsByAddressFilterRequest {

    @BeanParam
    @Valid
    AddressFilterRequest addressFilter;

    @BeanParam
    @Valid
    PaginationRequest pagination;

    @QueryParam("sortCreatedAt")
    @DefaultValue("Ascending")
    Sort.Direction sortCreatedAt;
}
