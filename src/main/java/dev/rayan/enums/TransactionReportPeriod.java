package dev.rayan.enums;

import lombok.Getter;

import java.time.*;

@Getter
public enum TransactionReportPeriod implements BaseEnum<TransactionReportPeriod> {

    CURRENT_YEAR(
            LocalDate.of(Year.now().getValue() - 1, MonthDay.now().getMonth().getValue(), MonthDay.now().getDayOfMonth()),
            "Current Year"
    );

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String value;

    TransactionReportPeriod(LocalDate startDate, String value) {
        this.startDate = startDate;
        //Is always current date
        this.endDate = LocalDate.now();
        this.value = value;
    }

    //Base enum getter
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }


}
