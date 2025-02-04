package dev.rayan.factory;

import dev.rayan.dto.response.transaction.TransactionReportResponse;
import dev.rayan.enums.TransactionReportPeriod;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public final class TxtReportAbstract extends ReportAbstractFile {

    @Override
    public void createReport(final TransactionReportResponse reportResponse, final TransactionReportPeriod period)
            throws IOException, IllegalAccessException {

        try (PrintWriter writer = new PrintWriter(super.createFile())) {
            createReportTitle(writer, period.toString());
            createReportInfo(writer, reportResponse.getFieldsAndValues());
        }

    }

    @Override
    public String getFileName() {
        return "report";
    }

    @Override
    public String getExtension() {
        return ".txt";
    }

    private void createReportTitle(final PrintWriter writer, final String period) {
        writer.printf("-> %s <- \n", period);
    }


    private void createReportInfo(final PrintWriter writer, final Map<String, String> fieldsAndValues) {
        fieldsAndValues.forEach((field, value) -> writer.printf("%s: %s \n", field, value));
    }

}


