package dev.rayan.report;

import dev.rayan.dto.respose.TransactionReportResponse;
import dev.rayan.enums.TransactionReportPeriod;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;

public abstract class ReportAbstractFile {

    private static final String USER_HOME = System.getProperty("user.home");

    private static final BiFunction<String, String, File> DOWNLOAD_PATH_FUNCTION = (fileName, extension) -> new File(
            String.format("%s/Downloads/%s%s", USER_HOME, fileName, extension)
    );

    public abstract void createReport(TransactionReportResponse response, TransactionReportPeriod period) throws IllegalAccessException, IOException;

    public abstract String getFileName();

    public abstract String getExtension();

    public File createDownloadPath() {

        File downloadPath = DOWNLOAD_PATH_FUNCTION.apply(getFileName(), getExtension());
        int version = 1;

        while (downloadPath.exists()) {
            downloadPath.renameTo(DOWNLOAD_PATH_FUNCTION.apply(getFileName() + " (" + version + ")", getExtension()));
            version++;
        }

        return downloadPath;
    }


}
