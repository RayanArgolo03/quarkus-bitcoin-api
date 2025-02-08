package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TransactionResponse(
        String currentValue,
        String valueDate,
        String units,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime transactionDate,
        String type,
        String total) {
}

