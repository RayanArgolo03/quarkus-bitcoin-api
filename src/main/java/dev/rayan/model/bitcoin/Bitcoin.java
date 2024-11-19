package dev.rayan.model.bitcoin;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor(force = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public final class Bitcoin {

    BigDecimal last;
    LocalDateTime time = LocalDateTime.now();

}
