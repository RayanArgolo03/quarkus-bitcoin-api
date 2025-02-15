package dev.rayan.report;

import dev.rayan.enums.TransactionReportFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportFileFactory {

    public static ReportAbstractFile createReportAbstractFile(final TransactionReportFormat reportFormat) {

        return switch (reportFormat) {
            case EXCEL -> new ExcelReport();
            case TXT -> new TxtReport();
            default -> throw new IllegalArgumentException("Invalit report format!");
        };

    }

}
