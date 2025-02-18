package dev.rayan.dto.request.transaction;

import io.quarkus.panache.common.Sort;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;

import java.util.List;

@Getter
public final class TransactionByTypeRequest {

    @QueryParam("type")
    List<@NotBlank(message = "Required type!") String> types;

    @QueryParam("sortType")
    @DefaultValue("Ascending")
    Sort.Direction sortType;

}
