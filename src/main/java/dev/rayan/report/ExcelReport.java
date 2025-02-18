package dev.rayan.report;

import dev.rayan.dto.response.transaction.TransactionReportResponse;
import dev.rayan.enums.TransactionReportPeriod;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;


public final class ExcelReport extends ReportAbstractFile {

    private final static String SHEET_NAME = "report";
    private final static String FONT_NAME = "Aptos Narrow";

    @Override
    public void createReport(final TransactionReportResponse response, final TransactionReportPeriod period)
            throws IllegalAccessException, IOException {

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream output = new FileOutputStream(super.createFile())) {

            final Sheet sheet = workbook.createSheet(SHEET_NAME);

            createReportTitle(workbook.createCellStyle(), sheet, workbook.createFont(), period.getValue());
            createReportInfo(workbook.createCellStyle(), sheet, workbook.createFont(), response.getFieldsAndValues());

            workbook.write(output);
        }
    }

    @Override
    protected String getExtension() {
        return ".xlsx";
    }

    private void createReportTitle(final CellStyle style, final Sheet sheet, final Font font, final String period) {

        //Creating the title rows cells and cells: A, B and C columns
        final int firstRow = 0, lastRow = 2;
        IntStream.rangeClosed(firstRow, lastRow).forEach(rowIndex -> {
            Row row = sheet.createRow(rowIndex);
            row.createCell(firstRow);
            row.createCell(lastRow - 1);
            row.createCell(lastRow);
        });

        //Merging cells - Merged
        final CellRangeAddress titleRange = CellRangeAddress.valueOf("A1:B2");
        sheet.addMergedRegion(titleRange);

        //Align cells in center
        alignCells(style, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        //Creating font format title
        formatFont(font, (short) 16, true, true, IndexedColors.DARK_RED);
        style.setFont(font);

        //Getting merged cell, adding style and title
        final Cell titleCell = sheet.getRow(firstRow).getCell(firstRow);
        titleCell.setCellStyle(style);
        titleCell.setCellValue(period);

        //Indent column
        formatIndent(sheet, firstRow);
    }


    private void createReportInfo(final CellStyle style, final Sheet sheet, final Font font, final Map<String, String> fieldsAndValues) {

        //Formating info font
        formatFont(font, (short) 14, false, false, null);
        style.setFont(font);

        //Aling cells
        alignCells(style, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        //Writing values
        int rowIndex = 2;
        final int fieldColumn = 0, attributeColumn = 1;

        for (String field : fieldsAndValues.keySet()) {

            Row row = sheet.createRow(rowIndex);

            //Create field and attribute columns
            formatCell(row.createCell(fieldColumn), style, field);

            String attribute = fieldsAndValues.get(field);
            formatCell(row.createCell(attributeColumn), style, attribute);

            rowIndex++;
        }

        //Indent columns
        formatIndent(sheet, fieldColumn, attributeColumn);
    }

    private void formatCell(final Cell cell, final CellStyle style, final String value) {
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void alignCells(final CellStyle style, final HorizontalAlignment horizontal, final VerticalAlignment vertical) {
        style.setAlignment(horizontal);
        style.setVerticalAlignment(vertical);
    }

    private void formatFont(final Font font, final short size, final boolean isBold, final boolean hasUndeline, final IndexedColors color) {

        font.setFontName(FONT_NAME);
        font.setFontHeightInPoints(size);
        font.setBold(isBold);

        if (hasUndeline) font.setUnderline(Font.U_SINGLE);
        if (color != null) font.setColor(color.getIndex());

    }

//    private void formatBorders(final CellRangeAddress titleRange, final Sheet sheet) {
//        RegionUtil.setBorderBottom(BorderStyle.THIN, titleRange, sheet);
//        RegionUtil.setBorderTop(BorderStyle.THIN, titleRange, sheet);
//        RegionUtil.setBorderRight(BorderStyle.THIN, titleRange, sheet);
//        RegionUtil.setBorderLeft(BorderStyle.THIN, titleRange, sheet);
//
//        /* Optional: setting borders colors
//        final int colorIndex = IndexedColors.BLACK.getIndex();
//        RegionUtil.setBottomBorderColor(colorIndex, titleRange, sheet);*/
//    }

    private void formatIndent(final Sheet sheet, final int... columns) {
        Arrays.stream(columns)
                .forEach(sheet::autoSizeColumn);
    }


}
