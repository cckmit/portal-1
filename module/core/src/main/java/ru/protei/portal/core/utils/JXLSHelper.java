package ru.protei.portal.core.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.winter.core.utils.collections.CollectionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class JXLSHelper {

    private interface ReportWriter<T> {
        int[] getColumnsWidth();
        String[] getColumnNames();
        Object[] getColumnValues(T object);
        Object[] getSumValues();
    }

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

    private static DateFormat getDateFormat() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
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

    private static <T> void writeReport(List<T> objects, OutputStream outputStream, Lang.LocalizedLang lang, ReportWriter<T> reportWriter) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook(/*-1 если надо отключить сброс в файл и хранить всё в памяти*/);
        Sheet sheet = workbook.createSheet();
        Font font = getDefaultFont(workbook);
        CellStyle thStyle = getTableHeaderStyle(workbook, font);
        CellStyle style = getDefaultStyle(workbook, font);
        CellStyle sumStyle = getSumStyle(workbook, font);

        setColumnsWidth(sheet, reportWriter.getColumnsWidth());
        makeHeader(sheet.createRow(0), thStyle, lang, reportWriter.getColumnNames());

        int rowIndex = 1;
        for (T object : objects) {
            Row row = sheet.createRow(rowIndex++);
            row.setRowStyle(style);
            fillRow(row, reportWriter.getColumnValues(object));
        }

        Object[] sumValues = reportWriter.getSumValues();
        if (CollectionUtils.isNotEmpty(sumValues)) {
            Row row = sheet.createRow(rowIndex++);
            fillRow(row, sumValues, sumStyle);
        }

        workbook.write(outputStream);
        workbook.dispose();
    }

    public static void writeIssuesReport(List<CaseObject> issues, OutputStream outputStream, Lang.LocalizedLang lang) throws IOException {
        final DateFormat dateFormat = getDateFormat();
        writeReport(issues, outputStream, lang, new ReportWriter<CaseObject>() {
            @Override
            public int[] getColumnsWidth() {
                return new int[] {
                        3650,
                        3430,
                        8570,
                        4590,
                        4000,
                        3430,
                        6000,
                        6000,
                        6000,
                        6000,
                        15000
                };
            }
            @Override
            public String[] getColumnNames() {
                return new String[] {
                        "ir_caseno",
                        "ir_private",
                        "ir_name",
                        "ir_created",
                        "ir_state",
                        "ir_importance",
                        "ir_company",
                        "ir_initiator",
                        "ir_product",
                        "ir_manager",
                        "ir_info"
                };
            }
            @Override
            public Object[] getColumnValues(CaseObject issue) {
                return new Object[] {
                        "CRM-" + issue.getCaseNumber(),
                        lang.get(issue.isPrivateCase() ? "yes" : "no"),
                        issue.getName(),
                        dateFormat.format(issue.getCreated()),
                        lang.get("case_state_" + String.valueOf(issue.getState().getId())),
                        lang.get("importance_" + String.valueOf(issue.getImpLevel())),
                        issue.getInitiatorCompany() != null ? issue.getInitiatorCompany().getCname() : "",
                        issue.getInitiator() != null ? issue.getInitiator().getDisplayShortName() : "",
                        issue.getProduct() != null ? issue.getProduct().getName() : "",
                        issue.getManager() != null ? issue.getManager().getDisplayShortName() : "",
                        issue.getInfo()
                };
            }
            @Override
            public Object[] getSumValues() {
                return null;
            }
        });
    }
}
