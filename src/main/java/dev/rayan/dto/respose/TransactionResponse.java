package dev.rayan.dto.respose;

public record TransactionResponse(
        String currentValue,
        String valueDate,
        String unitsPurchased,
        String purchaseDate,
        String total) {
}

