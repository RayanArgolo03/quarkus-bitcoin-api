package dev.rayan.dto.response.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.rayan.dto.request.page.PaginationRequest;

import java.util.List;


public record PageResponse(
        List<?> elements,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        PaginationRequest paginationRequest,
        boolean isSorted,
        int totalElementsInPage,
        int totalElements,
        int currentPage,
        int totalPages) {

    public PageResponse(List<?> elements,
                        PaginationRequest pagination,
                        boolean isSorted,
                        int totalElements,
                        int currentPage,
                        int totalPages) {
        this(
                elements,
                pagination,
                isSorted,
                elements.size(),
                totalElements,
                currentPage,
                totalPages
        );
    }
}
