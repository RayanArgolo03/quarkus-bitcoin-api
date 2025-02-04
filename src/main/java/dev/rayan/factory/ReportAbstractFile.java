package dev.rayan.factory;

import dev.rayan.dto.response.transaction.TransactionReportResponse;
import dev.rayan.enums.TransactionReportPeriod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ReportAbstractFile {

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String DOWNLOAD_FOLDER = "Downloads";

    public abstract void createReport(TransactionReportResponse response, TransactionReportPeriod period) throws IllegalAccessException, IOException;

    public abstract String getFileName();

    public abstract String getExtension();

    public File createFile() {

        int version = 0;
        File file = getFilePath(version);

        for (version = 1; file.exists(); version++) file = getFilePath(version);

        return file;
    }

    private File getFilePath(final int version) {

        final Path path = (version == 0)
                ? Paths.get(USER_HOME, DOWNLOAD_FOLDER, getFileName() + getExtension())
                : Paths.get(USER_HOME, DOWNLOAD_FOLDER, getFileName() + " (" + version + ")" + getExtension());

        return path.toFile();
    }


}
