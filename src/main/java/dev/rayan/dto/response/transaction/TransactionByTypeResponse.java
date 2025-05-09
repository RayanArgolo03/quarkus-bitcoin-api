package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.rayan.enums.TransactionType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@RegisterForReflection
@Builder
public record TransactionByTypeResponse(
        TransactionType type,
        Long transactionsMade,
        BigDecimal totalQuantity,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime firstTransactionDate,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime lastTransactionDate,
        String periodBetweenFirstAndLast
) {
}


