package dev.rayan.dto.respose;

public record TransactionResponse(
        String currentValue,
        String valueDate,
        String units,
        String transactionDate,
        String type,
        String total) {
}

