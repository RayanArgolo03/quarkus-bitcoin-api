package dev.rayan.enums;

import lombok.Getter;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;

@Getter
public enum TransactionReportPeriod implements BaseEnum<TransactionReportPeriod> {

    LAST_YEAR(
            "Last Year",
            LocalDate.of(Year.now().getValue() - 1, MonthDay.now().getMonth().getValue(), MonthDay.now().getDayOfMonth())
    );

    private final String value;
    private final LocalDate startDate;
    private final LocalDate endDate;

    TransactionReportPeriod(String value, LocalDate startDate) {
        this.value = value;
        this.startDate = startDate;
        this.endDate = LocalDate.now();
    }


}
