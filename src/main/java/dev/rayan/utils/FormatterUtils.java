package dev.rayan.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FormatterUtils {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance();

    public static String formatMoney(final BigDecimal value){  return NUMBER_FORMAT.format(value); }

}
