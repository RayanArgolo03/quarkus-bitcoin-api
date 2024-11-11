package dev.rayan.model.bitcoin;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public final class Bitcoin {

    UUID id;
    BigDecimal price;
    LocalDateTime priceDate;

}
