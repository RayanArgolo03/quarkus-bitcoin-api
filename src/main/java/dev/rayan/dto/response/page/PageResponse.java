package dev.rayan.dto.response.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.rayan.dto.request.page.PaginationRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public final class PageResponse<T> {

    List<T> elements;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    PaginationRequest pagination;

    boolean isSorted;
    int totalElements;
    int totalPages;

    public PageResponse(List<T> elements, PaginationRequest pagination, boolean isSorted, int totalPages) {
        this.elements = elements;
        this.pagination = pagination;
        this.isSorted = isSorted;
        this.totalElements = elements.size();
        this.totalPages = totalPages;
    }

}
