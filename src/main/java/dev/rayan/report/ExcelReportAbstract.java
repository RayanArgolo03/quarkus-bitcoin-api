package dev.rayan.report;

import dev.rayan.dto.respose.TransactionReportResponse;
import dev.rayan.enums.TransactionReportPeriod;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.stream.IntStream;


public final class ExcelReportAbstract extends ReportAbstractFile {

    private final static String FILE_NAME = "transactions_report";
    private final static String EXTENSION = ".xlsx";

    @Override
    public void generateReport(final TransactionReportResponse response, final TransactionReportPeriod period)
            throws IllegalAccessException, IOException {


        try (Workbook workbook = new XSSFWorkbook();
             OutputStream output = new FileOutputStream(createDownloadPath())) {

            final Sheet sheet = workbook.createSheet("report");

            //Todo precisam ser fontes e cell styles diferentes
            final CellStyle cellStyle = workbook.createCellStyle();
            final Font font = workbook.createFont();

            createReportTitle(cellStyle, sheet, font, period.toString());
            createReportInfo(cellStyle, sheet, font, response.getFieldsAndValues());

            workbook.write(output);
        }
    }

    private File createDownloadPath() {

        File downloadPath = DOWNLOAD_PATH_FUNCTION.apply(FILE_NAME, EXTENSION);
        long currentVersion = 1;

        while (downloadPath.exists()) {
            downloadPath = DOWNLOAD_PATH_FUNCTION.apply(FILE_NAME + " (" + currentVersion + ")", EXTENSION);
            currentVersion++;
        }

        return downloadPath;
    }

    private void createReportTitle(final CellStyle cellStyle, final Sheet sheet, final Font font, final String period) {

        //Creating the title rows cells and cells: A, B and C columns
        final int initRow = 0, finalRow = 2;
        IntStream.rangeClosed(initRow, finalRow).forEach(rowIndex -> {
            Row row = sheet.createRow(rowIndex);
            row.createCell(initRow);
            row.createCell(finalRow - 1);
            row.createCell(finalRow);
        });

        //Merging cells - Merged
        final CellRangeAddress titleRange = CellRangeAddress.valueOf("A1:B2");
        sheet.addMergedRegion(titleRange);

        //Align cells in center
        alignCells(cellStyle, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        //Adding borders style and color
        createBorders(titleRange, sheet);

       /* Optional: setting borders colors
        final int colorIndex = IndexedColors.BLACK.getIndex();
        RegionUtil.setBottomBorderColor(colorIndex, titleRange, sheet);*/

        //Creating font format title
        formatFont(font, (short) 16, true);
        cellStyle.setFont(font);

        //Getting merged cell, adding style and title
        final Cell titleCell = sheet.getRow(initRow).getCell(initRow);
        titleCell.setCellStyle(cellStyle);
        titleCell.setCellValue(period);

        //Indent column
        sheet.autoSizeColumn(initRow);
    }


    //Todo continuar corrigindo: precisa passar cellstyle diferente?
    private void createReportInfo(final CellStyle cellStyle, final Sheet sheet, final Font font, final Map<String, String> fieldsAndValues) {

        //Modifyning font format to fields and attributes
        formatFont(font, (short) 14, false);

        //Adding background solid pattern to paint
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //Aling cells
        alignCells(cellStyle, HorizontalAlignment.LEFT, VerticalAlignment.CENTER);

        //Row 4 in excel, after Report Title
        int rowIndex = 3;
        for (String field : fieldsAndValues.keySet()) {

            Row row = sheet.createRow(rowIndex);
            createFormattedCell(row.createCell(0), cellStyle, IndexedColors.ROYAL_BLUE.getIndex(), field);

            String attribute = fieldsAndValues.get(field);
            createFormattedCell(row.createCell(1), cellStyle, IndexedColors.ROSE.getIndex(), attribute);

            rowIndex++;
        }

        //Indent field and value row
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

    }

    private void alignCells(final CellStyle cellStyle, final HorizontalAlignment horizontal, final VerticalAlignment vertical) {
        cellStyle.setAlignment(horizontal);
        cellStyle.setVerticalAlignment(vertical);
    }

    private void formatFont(final Font font, final short size, final boolean isBold) {
        font.setFontName("Aptos Narrow");
        font.setFontHeightInPoints(size);
        font.setBold(isBold);
    }

    private void createBorders(final CellRangeAddress titleRange, final Sheet sheet) {
        RegionUtil.setBorderBottom(BorderStyle.THIN, titleRange, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, titleRange, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, titleRange, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, titleRange, sheet);
    }

    private void createFormattedCell(final Cell cell, final CellStyle cellStyle, final short colorIndex, final String value) {

        //Setting background color
        cellStyle.setFillForegroundColor(colorIndex);

        //Creating and format
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
    }

}
