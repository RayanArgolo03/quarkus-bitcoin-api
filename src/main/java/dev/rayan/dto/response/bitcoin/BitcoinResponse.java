package dev.rayan.dto.response.bitcoin;


import com.fasterxml.jackson.annotation.*;
import dev.rayan.utils.FormatterUtils;
import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BitcoinResponse(

        //Serialize as "last" and ignore bigdecimal deserialising
        @JsonProperty(value = "last")
        @JsonIgnore
        BigDecimal price,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime quotedAt
) {

    //Initing quotedAt after deserialize
    public BitcoinResponse {
        quotedAt = LocalDateTime.now();
    }

    //Deserialise as price in money format
    @JsonProperty("price")
    public String priceFormatted() {
        return FormatterUtils.formatMoney(price);
    }
}
