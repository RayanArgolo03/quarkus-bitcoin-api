package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TransactionByFilterResponse(
        String type,
        String quantity,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime madeAt) {
}
