package dev.rayan.dto.respose;

public record TransactionSummaryByFiltersResponse(
        String madeAt,
        String quantity,
        String type) {
}
