package dev.rayan.factory;

import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.report.ExcelReportAbstract;
import dev.rayan.report.ReportAbstractFile;
import dev.rayan.report.TxtReportAbstract;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportFileFactory {

    public static ReportAbstractFile createReportAbstractFile(final TransactionReportFormat reportFormat) {

        return switch (reportFormat) {
            case EXCEL -> new ExcelReportAbstract();
            case TXT -> new TxtReportAbstract();
            default -> throw new IllegalArgumentException("Invalit report format!");
        };

    }

}
