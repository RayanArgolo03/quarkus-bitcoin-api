package dev.rayan.dto.response.transaction;

public record TransactionSummaryByFiltersResponse(
        String madeAt,
        String quantity,
        String type) {
}
