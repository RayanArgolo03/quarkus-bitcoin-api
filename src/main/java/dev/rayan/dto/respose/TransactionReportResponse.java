package dev.rayan.dto.respose;

public record TransactionReportResponse(
        String transactionsMade,
        String totalPurchased,
        String currentValuePurchased,
        String valueDate,
        String firstPurchase,
        String lastPurchase,
        String totalSold,
        String currentValueSold,
        String firstSold,
        String lastSold,
        String lastTransaction) {}
