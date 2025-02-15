package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.rayan.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String bitcoinCurrentValue,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate currentValueDate,
        String quantity,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime transactionDate,
        String type,
        String transactionTotal) {
}

