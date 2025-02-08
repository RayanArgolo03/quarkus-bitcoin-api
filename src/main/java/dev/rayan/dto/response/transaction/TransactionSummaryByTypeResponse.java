package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;


@RegisterForReflection
public record TransactionSummaryByTypeResponse(
        String type,
        String transactionsMade,
        String quantity,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime first,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime last,
        String periodBetweenFirstAndLast
) {
}


