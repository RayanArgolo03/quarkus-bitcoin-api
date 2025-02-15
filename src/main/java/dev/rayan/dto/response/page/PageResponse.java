package dev.rayan.dto.response.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.rayan.dto.request.page.PaginationRequest;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;


public record PageResponse(
        List<?> elements,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        PaginationRequest pagination,
        boolean isSorted,
        int totalElements,
        int totalPages
//        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
//        LocalDateTime madeAt
) {

    public PageResponse(List<?> elements, PaginationRequest pagination, boolean isSorted, int totalPages) {
        this(
                elements,
                pagination,
                isSorted,
                elements.size(),
                totalPages
//                LocalDateTime.now()
        );
    }
}
