package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

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
        @JsonProperty("madeAt")
        LocalDateTime createdAt,
        String type,
        String transactionTotal) {
}

