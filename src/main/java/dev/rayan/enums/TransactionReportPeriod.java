package dev.rayan.enums;

import dev.rayan.utils.DateUtils;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public enum TransactionReportPeriod {

    CURRENT_YEAR(
            LocalDate.of(DateUtils.YEAR_NUMBER - 1, DateUtils.MONTH_NUMBER, DateUtils.DAY_NUMBER),
            LocalDate.now(),
            "Current year"
    );

    private final LocalDate initDate;
    private final LocalDate finalDate;
    private final String value;

    TransactionReportPeriod(LocalDate initDate, LocalDate finalDate, String value) {
        this.initDate = initDate;
        this.finalDate = finalDate;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
