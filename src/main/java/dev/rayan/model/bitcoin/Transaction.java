package dev.rayan.model.bitcoin;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public final class Transaction {

    UUID id;
    BigDecimal bitcoinQuantity;
    LocalDateTime date;

}
