package dev.rayan.report;

import dev.rayan.dto.respose.TransactionReportResponse;

public interface ReportFile {
    void generateReport(TransactionReportResponse response);
}
