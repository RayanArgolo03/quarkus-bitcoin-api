package dev.rayan.utils;

import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FormatterUtils {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    public static String formatMoney(final BigDecimal value) {
        return NUMBER_FORMAT.format(value);
    }

    public static String formatDate(final LocalDateTime dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }

}
