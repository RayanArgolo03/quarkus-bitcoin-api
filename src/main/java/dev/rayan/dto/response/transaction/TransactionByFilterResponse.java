package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.rayan.enums.TransactionType;

import java.time.LocalDateTime;

public record TransactionByFilterResponse(
        TransactionType type,
        String quantity,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime madeAt) {
}
