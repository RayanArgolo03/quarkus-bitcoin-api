package dev.rayan.dto.response.bitcoin;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.text.NumberFormat;
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
        return NumberFormat.getCurrencyInstance()
                .format(price);
    }
}
