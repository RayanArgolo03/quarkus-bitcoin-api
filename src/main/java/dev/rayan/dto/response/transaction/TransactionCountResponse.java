package dev.rayan.dto.response.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionCountResponse(
        @JsonProperty("transactionsInPeriod")
        @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
        long count
) {
}
