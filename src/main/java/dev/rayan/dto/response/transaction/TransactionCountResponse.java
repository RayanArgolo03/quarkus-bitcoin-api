package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;

public record TransactionCountResponse(
        @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
        long transactionsInPeriod
) {
}
