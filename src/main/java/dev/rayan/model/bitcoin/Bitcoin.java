package dev.rayan.model.bitcoin;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(force = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public final class Bitcoin {

    UUID id;
    String pair;
    BigDecimal last;
    BigDecimal volume24h;
    BigDecimal var24h;

    //Todo pesquise sobre desserialização pelo Jackson e anote
    @JsonFormat(pattern = "YYYY-MM-dd'T'HH:mm:ss")
    LocalDateTime time;


}
