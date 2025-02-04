package dev.rayan.dto.request.page;

import io.quarkus.panache.common.Page;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

public record PaginationRequest(

        @QueryParam("pageIndex")
        @DefaultValue("0")
        @PositiveOrZero(message = "Page index can´t be less than 0!")
        int pageIndex,

        @QueryParam("pageSize")
        @DefaultValue("10")
        @Min(message = "Page size can´t be less than 1!", value = 1)
        int pageSize
) {

    public Page getPage() {
        return Page.of(pageIndex, pageSize);
    }

}
