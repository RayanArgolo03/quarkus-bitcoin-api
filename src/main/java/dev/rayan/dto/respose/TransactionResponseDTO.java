package dev.rayan.dto.respose;

public record TransactionResponseDTO(
        String currentValue,
        String valueDate,
        String unitsPurchased,
        String purchaseDate,
        String total) {
}

