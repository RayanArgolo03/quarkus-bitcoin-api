package dev.rayan.dto.response.transaction;

public record TransactionResponse(
        String currentValue,
        String valueDate,
        String units,
        String transactionDate,
        String type,
        String total) {
}

