package dev.rayan.dto.request.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.panache.common.Page;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

public record PaginationRequest(

        @QueryParam("pageNumber")
        @DefaultValue("1")
        @Min(message = "Page number can´t be less than 1!", value = 1L)
        int pageNumber,

        @QueryParam("pageSize")
        @DefaultValue("10")
        @Min(message = "Page size can´t be less than 1!", value = 1L)
        @Max(message = "Page size can´t be greater than 100!", value = 100L)
        int pageSize

) {

    @JsonIgnore
    public Page getPage() {
        return new Page(pageIndex(), pageSize);
    }

    public int pageIndex() {
        return pageNumber - 1;
    }

}