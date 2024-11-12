package dev.rayan.dto.respose;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BitcoinQuoteResponse(String currentPrice,
                                   String lastPriceDate) {
}
