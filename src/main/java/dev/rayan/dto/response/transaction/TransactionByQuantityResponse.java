package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.rayan.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionByQuantityResponse(
        TransactionType type,
        BigDecimal quantity,
        String clientEmail,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        @JsonProperty("madeAt")
        LocalDateTime createdAt
) {
}
