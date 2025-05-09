package dev.rayan.utils;

import dev.rayan.dto.request.page.PaginationRequest;
import dev.rayan.dto.response.page.PageResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationUtils {

    public static PageResponse paginate(final PanacheQuery<?> elements, final PaginationRequest paginationRequest) {

        final int totalElements = (int) elements.count();

        if (paginationRequest == null) {
            return new PageResponse(elements.list(), null, true, totalElements, 1, 1);
        }

        if (totalElements == 0) {
            return new PageResponse(elements.list(), null, false, totalElements, 0, 0);
        }

        elements.page(paginationRequest.getPage());

        final int totalPages = elements.pageCount();
        final int lastPageIndex = totalPages - 1;
        final int desiredPageIndex = paginationRequest.pageIndex();

        if (desiredPageIndex > lastPageIndex) {
            elements.lastPage();
        }

        final int currentPage = elements.page().index + 1;

        return new PageResponse(elements.list(), paginationRequest, true, totalElements, currentPage, totalPages);
    }

}
