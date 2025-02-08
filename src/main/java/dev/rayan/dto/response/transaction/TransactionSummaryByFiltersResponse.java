package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TransactionSummaryByFiltersResponse(
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime madeAt,
        String quantity,
        String type) {
}
