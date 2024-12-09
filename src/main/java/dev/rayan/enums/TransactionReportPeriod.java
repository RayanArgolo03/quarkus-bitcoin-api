package dev.rayan.enums;

import dev.rayan.enums.interfaces.BaseEnum;
import lombok.Getter;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;

@Getter
public enum TransactionReportPeriod implements BaseEnum<TransactionReportPeriod> {

    CURRENT_YEAR(
            LocalDate.of(Year.now().getValue() - 1, MonthDay.now().getMonth().getValue(), MonthDay.now().getDayOfMonth()),
            LocalDate.now(),
            "Current Year"
    );

    private final LocalDate initDate;
    private final LocalDate finalDate;
    private final String value;

    TransactionReportPeriod(LocalDate initDate, LocalDate finalDate, String value) {
        this.initDate = initDate;
        this.finalDate = finalDate;
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
