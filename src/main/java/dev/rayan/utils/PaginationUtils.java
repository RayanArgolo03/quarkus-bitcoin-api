package dev.rayan.utils;

import dev.rayan.dto.request.page.PaginationRequest;
import dev.rayan.dto.response.page.PageResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationUtils {

    public static <T> PageResponse<T> paginate(PanacheQuery<T> elements, final PaginationRequest pagination) {

        if (elements.count() == 0L) {
            return buildPageResponse(elements.list(), null, false, 0);
        }

        elements = elements.page(pagination.getPage());

        final int totalPages = elements.pageCount();
        final int lastPageIndex = totalPages - 1;
        final int desiredPageIndex = pagination.getPageIndex();

        if (desiredPageIndex > lastPageIndex) {
            elements = elements.lastPage();
            pagination.setPageNumber(totalPages);
        }

        return buildPageResponse(elements.list(), pagination, true, totalPages);
    }

    private static <T> PageResponse<T> buildPageResponse(final List<T> elements,
                                                         final PaginationRequest pagination,
                                                         final boolean isSorted,
                                                         final int totalPages) {

        return new PageResponse<>(elements, pagination, isSorted, totalPages);
    }

}
