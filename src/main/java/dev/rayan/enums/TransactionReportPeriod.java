package dev.rayan.enums;

import dev.rayan.enums.interfaces.BaseEnum;
import lombok.Getter;

import java.time.*;

@Getter
public enum TransactionReportPeriod implements BaseEnum<TransactionReportPeriod> {

    CURRENT_YEAR(
            LocalDateTime.of(
                    LocalDate.of(Year.now().getValue() - 1, MonthDay.now().getMonth().getValue(), MonthDay.now().getDayOfMonth()),
                    LocalTime.now()
            ),
            "Current Year"
    );

    private final LocalDateTime initDate;
    private final LocalDateTime finalDate;
    private final String value;

    TransactionReportPeriod(LocalDateTime initDate, String value) {
        this.initDate = initDate;
        //Is always current date
        this.finalDate = LocalDateTime.now();
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
