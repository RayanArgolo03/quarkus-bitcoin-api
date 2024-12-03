package dev.rayan.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {
    public static final int YEAR_NUMBER = Year.now().getValue();
    public static final int MONTH_NUMBER = MonthDay.now().getMonthValue();
    public static final int DAY_NUMBER = MonthDay.now().getDayOfMonth();
}
