package dev.rayan.report;

import dev.rayan.enums.TransactionReportFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportFactory {

    public static ReportAbstractFile createReportFile(final TransactionReportFormat format) {
        return switch (format) {
            case EXCEL -> new ExcelReport();
            case TXT -> new TxtReport();
        };
    }

}
