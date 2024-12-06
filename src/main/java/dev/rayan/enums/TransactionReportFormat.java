package dev.rayan.enums;

import dev.rayan.enums.interfaces.BaseEnum;

public enum TransactionReportFormat implements BaseEnum<TransactionReportFormat> {

    EXCEL("Excel"),
    TXT("Txt");

    private final String value;

    TransactionReportFormat(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
