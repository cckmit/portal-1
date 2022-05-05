package ru.protei.portal.core.utils;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import ru.protei.portal.core.Lang;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class JXLSHelper {

    public static class ReportBook<T> {
        private final SXSSFWorkbook workbook;
        private final Writer<T> writer;
        private final Lang.LocalizedLang lang;
        private final Map<Integer, SXSSFSheet> sheetMap;
        private final Map<Integer, Integer> sheetRowIndexMap;
        private final Map<Integer, Font> fontsMap;
        private final Map<Integer, CellStyle> cellStylesMap;
        private Font defaultFont;
        private CellStyle defaultHeaderCellStyle;
        private CellStyle defaultCellStyle;
        private int sheetIndex = 0;

        public interface Writer<T> {
            int[] getColumnsWidth();
            String[] getLangColumnNames();
            default String[] getColumnNames() { return new String[0];}
            Object[] getColumnValues(T object);
            default CellStyle getCellStyle(Workbook workbook, int columnIndex) {
                return null;
            }
        }

        public ReportBook(Lang.LocalizedLang lang, Writer<T> writer) {
            this.lang = lang;
            this.workbook = new SXSSFWorkbook(/*-1 если надо отключить сброс в файл и хранить всё в памяти*/);
            this.writer = writer;
            this.sheetMap = new HashMap<>();
            this.sheetRowIndexMap = new HashMap<>();
            this.fontsMap = new HashMap<>();
            this.cellStylesMap = new HashMap<>();
            this.defaultFont = getDefaultFont();
            this.defaultHeaderCellStyle = getDefaultTableHeaderStyle(defaultFont);
            this.defaultCellStyle = getDefaultStyle(defaultFont);
        }

        public int createSheet() {
            SXSSFSheet sheet = workbook.createSheet();
            setColumnsWidth(sheet, writer.getColumnsWidth());
            makeHeader(sheet.createRow(0), defaultHeaderCellStyle, lang, writer.getLangColumnNames(), writer.getColumnNames());
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
            for (T object : objects) {
                write(sheetNumber, object);
            }
        }

        public void write(int sheetNumber, T object) {
            Integer rowNumber = sheetRowIndexMap.get(sheetNumber);
            Row row = sheetMap.get(sheetNumber).createRow(rowNumber++);
            fillRow(workbook, row, writer.getColumnValues(object), this::getCellStyleForColumnIndex);
            sheetRowIndexMap.put(sheetNumber, rowNumber);
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

        public Font makeFont(int identifier, Consumer<Font> init) {
            Font font = fontsMap.get(identifier);
            if (font != null) {
                return font;
            }
            font = workbook.createFont();
            init.accept(font);
            fontsMap.put(identifier, font);
            return font;
        }

        public CellStyle makeCellStyle(int identifier, Consumer<CellStyle> init) {
            CellStyle cellStyle = cellStylesMap.get(identifier);
            if (cellStyle != null) {
                return cellStyle;
            }
            cellStyle = workbook.createCellStyle();
            init.accept(cellStyle);
            cellStylesMap.put(identifier, cellStyle);
            return cellStyle;
        }

        private CellStyle getCellStyleForColumnIndex(int columnIndex) {
            CellStyle cellStyle = writer.getCellStyle(workbook, columnIndex);
            if (cellStyle != null) {
                return cellStyle;
            }
            return defaultCellStyle;
        }

        public Font getDefaultFont() {
            if (defaultFont != null) {
                return defaultFont;
            }
            defaultFont = workbook.createFont();
            defaultFont.setFontName("Calibri");
            defaultFont.setFontHeightInPoints((short) 11);
            return defaultFont;
        }

        public CellStyle getDefaultTableHeaderStyle(Font font) {
            if (defaultHeaderCellStyle != null) {
                return defaultHeaderCellStyle;
            }
            defaultHeaderCellStyle = workbook.createCellStyle();
            defaultHeaderCellStyle.setFont(font);
            defaultHeaderCellStyle.setAlignment(HorizontalAlignment.CENTER);
            defaultHeaderCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            defaultHeaderCellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
            defaultHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            return defaultHeaderCellStyle;
        }

        public CellStyle getDefaultStyle(Font font) {
            if (defaultCellStyle != null) {
                return defaultCellStyle;
            }
            defaultCellStyle = workbook.createCellStyle();
            defaultCellStyle.setFont(font);
            defaultCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            return defaultCellStyle;
        }

        private static void setColumnsWidth(Sheet sheet, int[] columnsWidth) {
            int columnIndex = 0;
            for (int width : columnsWidth) {
                sheet.setColumnWidth(columnIndex++, width);
            }
        }

        private static void makeHeader(Row row, CellStyle style, Lang.LocalizedLang lang,
                                       String[] langColumnNames, String[] columnNames) {
            int columnIndex = 0;
            for (String name : langColumnNames) {
                Cell cell = row.createCell(columnIndex++);
                cell.setCellValue(lang.get(name));
                cell.setCellStyle(style);
            }
            for (String name : columnNames) {
                Cell cell = row.createCell(columnIndex++);
                cell.setCellValue(name);
                cell.setCellStyle(style);
            }
        }

        private static void fillRow(SXSSFWorkbook workbook, Row row, Object[] values, Function<Integer, CellStyle> cellStyleProvider) {
            for (int columnIndex = 0; columnIndex < values.length; columnIndex++) {
                Cell cell = row.createCell(columnIndex);
                cell.setCellStyle(cellStyleProvider.apply(columnIndex));
                Object value = values[columnIndex];
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else if (value instanceof LinkData) {
                    Hyperlink hyperlink = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
                    hyperlink.setAddress(((LinkData) value).getUrl());
                    cell.setHyperlink(hyperlink);
                    cell.setCellValue(((LinkData) value).getLinkName());
                } else {
                    if (value == null) value = "";
                    cell.setCellValue(value.toString());
                }
            }
        }
    }
}
