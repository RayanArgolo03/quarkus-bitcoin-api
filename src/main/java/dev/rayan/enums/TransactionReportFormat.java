package dev.rayan.enums;

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

}
