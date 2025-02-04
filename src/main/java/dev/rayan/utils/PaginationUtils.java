package dev.rayan.utils;

import dev.rayan.dto.request.page.PaginationRequest;
import dev.rayan.dto.response.page.PageResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationUtils {

    public static <T> PageResponse<T> of(final List<T> elements, final PaginationRequest paginationRequest, final boolean isSorted) {
        return new PageResponse<>(elements, paginationRequest, isSorted);
    }

}
