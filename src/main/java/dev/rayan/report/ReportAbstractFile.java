package dev.rayan.report;

import dev.rayan.dto.respose.TransactionReportResponse;
import dev.rayan.enums.TransactionReportPeriod;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;

public abstract class ReportAbstractFile {

    private static final String USER_HOME = System.getProperty("user.home");

    protected static final BiFunction<String, String, File> DOWNLOAD_PATH_FUNCTION = (FILE_NAME, EXTENSION) -> new File(
            String.format("%s/Downloads/%s%s", USER_HOME, FILE_NAME, EXTENSION)
    );

    public abstract void createReport(TransactionReportResponse response, TransactionReportPeriod period) throws IllegalAccessException, IOException;

}
