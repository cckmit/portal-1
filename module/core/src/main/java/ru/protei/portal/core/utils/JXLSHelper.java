package ru.protei.portal.core.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ru.protei.portal.core.Lang;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.*;

import static org.apache.poi.ss.usermodel.DateUtil.SECONDS_PER_DAY;

public final class JXLSHelper {
    public interface ExcelFormat {
        String STANDARD = "Standard";
        String DATE_TIME = "DD.MM.YY HH:MM";
        String INFINITE_HOURS_MINUTES = "[H]:MM";
    }

    // -----------
    // Report book
    // -----------

    public static class ReportBook<T> {

        private final SXSSFWorkbook workbook;
        private final Writer<T> writer;
        private final Lang.LocalizedLang lang;
        private final Map<Integer, SXSSFSheet> sheetMap;
        private final Map<Integer, Integer> sheetRowIndexMap;
        private int sheetIndex = 0;

        public interface Writer<T> {
            int[] getColumnsWidth();
            String[] getColumnNames();
            Object[] getColumnValues(T object);

            default String[] getFormats() {
                String[] formats = new String[getColumnNames().length];

                Arrays.fill(formats, ExcelFormat.STANDARD);

                return formats;
            }
        }

        public ReportBook(Lang.LocalizedLang lang, Writer<T> writer) {
            this.lang = lang;
            this.workbook = new SXSSFWorkbook(/*-1 если надо отключить сброс в файл и хранить всё в памяти*/);
            this.writer = writer;
            this.sheetMap = new HashMap<>();
            this.sheetRowIndexMap = new HashMap<>();
        }

        public int createSheet() {
            SXSSFSheet sheet = workbook.createSheet();
            CellStyle thStyle = getTableHeaderStyle(workbook, getDefaultFont(workbook));
            setColumnsWidth(sheet, writer.getColumnsWidth());
            makeHeader(sheet.createRow(0), thStyle, lang, writer.getColumnNames());
            sheetMap.put(sheetIndex, sheet);
            sheetRowIndexMap.put(sheetIndex, 1);
            return sheetIndex++;
        }

        public void setSheetName(int sheetNumber, String name) {
            int sheetIndex = workbook.getSheetIndex(sheetMap.get(sheetNumber));
            name = WorkbookUtil.createSafeSheetName(name);
            workbook.setSheetName(sheetIndex, name);
        }

        public void write(int sheetNumber, List<T> objects) {
            CellStyle style = getDefaultStyle(workbook, getDefaultFont(workbook));
            String[] formats = writer.getFormats();
            for (T object : objects) {
                Integer rowNumber = sheetRowIndexMap.get(sheetNumber);
                Row row = sheetMap.get(sheetNumber).createRow(rowNumber++);
                row.setRowStyle(style);
                fillRow(row, writer.getColumnValues(object), workbook, formats);
                sheetRowIndexMap.put(sheetNumber, rowNumber);
            }
        }

        public void collect(OutputStream outputStream) throws IOException {
            workbook.write(outputStream);
        }

        public void close() throws IOException {
            workbook.dispose();
            workbook.close();
            sheetMap.clear();
            sheetRowIndexMap.clear();
            sheetIndex = 0;
        }
    }

    // --------------
    // Core mechanism
    // --------------

    private static Font getDefaultFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        return font;
    }

    private static CellStyle getTableHeaderStyle(Workbook workbook, Font font) {
        CellStyle style = workbook.createCellStyle();
        {
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }

    private static CellStyle getDefaultStyle(Workbook workbook, Font font) {
        CellStyle style = workbook.createCellStyle();
        {
            style.setFont(font);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
        }
        return style;
    }

    private static CellStyle getSumStyle(Workbook workbook, Font font) {
        CellStyle style = workbook.createCellStyle();
        {
            style.setFont(font);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.MEDIUM);
        }
        return style;
    }

    private static void setColumnsWidth(Sheet sheet, int[] columnsWidth) {
        int columnIndex = 0;
        for (int width : columnsWidth) {
            sheet.setColumnWidth(columnIndex++, width);
        }
    }

    private static void makeHeader(Row row, CellStyle style, Lang.LocalizedLang lang, String[] columnNames) {
        int columnIndex = 0;
        for (String name : columnNames) {
            Cell cell = row.createCell(columnIndex++);
            cell.setCellValue(lang.get(name));
            cell.setCellStyle(style);
        }
    }

    private static void fillRow(Row row, Object[] values, SXSSFWorkbook workbook, String[] formats) {
        int columnIndex = 0;
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            String format = formats[i];

            Cell cell = row.createCell(columnIndex++);

            CellStyle cellStyle = getDefaultStyle(workbook, getDefaultFont(workbook));
            cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));

            cell.setCellStyle(cellStyle);

            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof Date) {
                setDateFormattedCellValue(cell, format, workbook, (Date) value);
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }

    private static void setDateFormattedCellValue(Cell cell, String format, SXSSFWorkbook workbook, Date date) {
        CellStyle cellStyle = getDefaultStyle(workbook, getDefaultFont(workbook));
        cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));

        cell.setCellStyle(cellStyle);
        cell.setCellValue(date);
    }
}
