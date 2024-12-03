package dev.rayan.enums;

import lombok.Getter;

@Getter
public enum TransactionReportFormat {
    EXCEL("Excel"), TXT("Txt");

    private final String value;

    TransactionReportFormat(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
