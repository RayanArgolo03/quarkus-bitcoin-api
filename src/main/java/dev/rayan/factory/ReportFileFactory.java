package dev.rayan.factory;

import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.report.ExcelReport;
import dev.rayan.report.ReportFile;
import dev.rayan.report.TxtReport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportFileFactory {

    public static ReportFile createReportFile(final TransactionReportFormat format) {

        return switch (format) {
            case EXCEL -> new ExcelReport();
            case TXT -> new TxtReport();
            default -> throw new IllegalArgumentException("Invalit report format!");
        };

    }

}
