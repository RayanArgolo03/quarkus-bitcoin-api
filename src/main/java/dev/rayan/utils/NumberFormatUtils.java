package dev.rayan.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberFormatUtils {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance();

    public static String formatNumber(final BigDecimal number) {
        return NUMBER_FORMAT.format(number);
    }

}
