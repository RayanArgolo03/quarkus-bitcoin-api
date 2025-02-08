package dev.rayan.dto.response.bitcoin;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BitcoinResponse(
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        @JsonProperty("last")
        BigDecimal price,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime quotedAt
) {

    //Initing quotedAt after deserialize
    public BitcoinResponse {
        quotedAt = LocalDateTime.now();
    }
}
