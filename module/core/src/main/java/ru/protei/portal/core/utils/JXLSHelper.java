package ru.protei.portal.core.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ru.protei.portal.core.Lang;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JXLSHelper {

    // -----------
    // Report book
    // -----------

    public static class ReportBook<T> {

        private final SXSSFWorkbook workbook;
        private final Writer<T> writer;
        private final Lang.LocalizedLang lang;
        private final Map<Integer, SXSSFSheet> sheetMap;
        private int sheetIndex = 0;
        private int rowIndex = 0;

        public interface Writer<T> {
            int[] getColumnsWidth();
            String[] getColumnNames();
            Object[] getColumnValues(T object);
        }

        public ReportBook(Lang.LocalizedLang lang, Writer<T> writer) {
            this.lang = lang;
            this.workbook = new SXSSFWorkbook(/*-1 если надо отключить сброс в файл и хранить всё в памяти*/);
            this.writer = writer;
            this.sheetMap = new HashMap<>();
        }

        public int createSheet() {
            SXSSFSheet sheet = workbook.createSheet();
            CellStyle thStyle = getTableHeaderStyle(workbook, getDefaultFont(workbook));
            setColumnsWidth(sheet, writer.getColumnsWidth());
            makeHeader(sheet.createRow(rowIndex++), thStyle, lang, writer.getColumnNames());
            sheetMap.put(sheetIndex, sheet);
            return sheetIndex++;
        }

        public void setSheetName(int sheetNumber, String name) {
            int sheetIndex = workbook.getSheetIndex(sheetMap.get(sheetNumber));
            workbook.setSheetName(sheetIndex, name);
        }

        public void write(int sheetNumber, List<T> objects) {
            CellStyle style = getDefaultStyle(workbook, getDefaultFont(workbook));
            for (T object : objects) {
                Row row = sheetMap.get(sheetNumber).createRow(rowIndex++);
                row.setRowStyle(style);
                fillRow(row, writer.getColumnValues(object));
            }
        }

        public void collect(OutputStream outputStream) throws IOException {
            workbook.write(outputStream);
            close();
        }

        public void close() throws IOException {
            workbook.dispose();
            workbook.close();
            sheetMap.clear();
            sheetIndex = 0;
            rowIndex = 0;
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

    private static void fillRow(Row row, Object[] values) {
        fillRow(row, values, null);
    }

    private static void fillRow(Row row, Object[] values, CellStyle style) {
        int columnIndex = 0;
        for (Object value : values) {
            Cell cell = row.createCell(columnIndex++);
            if (style != null) {
                cell.setCellStyle(style);
            }
            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof Date) {
                cell.setCellValue((Date) value);
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }
}
