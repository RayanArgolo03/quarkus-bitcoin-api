package dev.rayan.dto.response.transaction;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.rayan.utils.NumberFormatUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BitcoinResponse(

        //Deserialise price as "last" in BrasilBitcoin API
        @JsonProperty(value = "last", access = JsonProperty.Access.WRITE_ONLY)
        BigDecimal price,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime quotedAt) {

    //Initiating quotedAt after deserialise
    public BitcoinResponse { quotedAt = LocalDateTime.now();}

    //Serialise "price" as money format - XX.XX R$
    @JsonProperty("price")
    @JsonIgnore
    public String getPriceFormatted() {
        return NumberFormatUtils.formatNumber(price);
    }
}
