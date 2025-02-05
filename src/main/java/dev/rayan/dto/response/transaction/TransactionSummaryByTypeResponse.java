package dev.rayan.dto.response.transaction;

import io.quarkus.runtime.annotations.RegisterForReflection;


@RegisterForReflection
public record TransactionSummaryByTypeResponse(
        String type,
        String transactionsMade,
        String quantity,
        String first,
        String last,
        String periodBetweenFirstAndLast) {

}


