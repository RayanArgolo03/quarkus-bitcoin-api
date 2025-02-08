package dev.rayan.dto.response.page;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.rayan.dto.request.page.PaginationRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;


public record PageResponse<T>(
        List<T> elements,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        PaginationRequest pagination,
        boolean isSorted,
        int totalElements,
        int totalPages,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime madeAt
) {

    public PageResponse(List<T> elements, PaginationRequest pagination, boolean isSorted, int totalPages) {
        this(elements, pagination, isSorted, elements.size(), totalPages, LocalDateTime.now());
    }
}
