package dev.rayan.dto.response.page;

import dev.rayan.dto.request.page.PaginationRequest;

import java.util.List;

//Todo converter em classe
public record PageResponse<T>(

        List<T> elements,
        PaginationRequest paginationRequest,
        boolean isSorted,
        long totalElements,
        long totalPages) {


    public PageResponse(List<T> elements, PaginationRequest paginationRequest, boolean isSorted) {
        this(
                elements,
                paginationRequest,
                isSorted,
                elements.size(),
                Math.max(elements.size() / paginationRequest.pageSize(), 1)
        );
    }
}
