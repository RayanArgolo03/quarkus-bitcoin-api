package dev.rayan.dto.request.transaction;

import dev.rayan.enums.TransactionType;
import dev.rayan.validation.EnumValidator;
import io.quarkus.panache.common.Sort;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

import java.util.List;

public record TransactionByTypeRequest(

        @QueryParam("type")
        List<@NotBlank(message = "Required type!")
        @EnumValidator(message = "Invalid transaction type!", enumClass = TransactionType.class)
                String> types,

        @QueryParam("sortType")
        @DefaultValue("Ascending")
        Sort.Direction sortType
) {
}
