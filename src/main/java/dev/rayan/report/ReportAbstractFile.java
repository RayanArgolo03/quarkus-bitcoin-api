package dev.rayan.report;

import dev.rayan.dto.response.transaction.TransactionReportResponse;
import dev.rayan.enums.TransactionReportPeriod;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ReportAbstractFile {

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String DOWNLOAD_FOLDER = "Downloads";
    private static final String FILE_NAME = "transactions_report";

    protected abstract String getExtension();

    public abstract void createReport(TransactionReportResponse response, TransactionReportPeriod period)
            throws IllegalAccessException, IOException;

    protected File createFile() {

        File file = getFilePath(0);

        for (int version = 1; file.exists(); version++) {
            file = getFilePath(version);
        }

        return file;
    }

    private File getFilePath(final int version) {

        final Path path = (version == 0)
                ? Paths.get(USER_HOME, DOWNLOAD_FOLDER, FILE_NAME + getExtension())
                : Paths.get(USER_HOME, DOWNLOAD_FOLDER, FILE_NAME + " (" + version + ")" + getExtension());

        return path.toFile();
    }


}
