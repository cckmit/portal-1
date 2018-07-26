package ru.protei.portal.core.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.winter.core.utils.collections.CollectionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class JXLSHelper {

    private interface ReportWriter {
        int[] getColumnsWidth();
        String[] getColumnNames();
        Object[] getColumnValues(int index);
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
        return new SimpleDateFormat("dd.MM.yyyy HH:mm");
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

    private static <T> void writeReport(List<T> objects, OutputStream outputStream, Lang.LocalizedLang lang, ReportWriter reportWriter) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook(/*-1 если надо отключить сброс в файл и хранить всё в памяти*/);
        Sheet sheet = workbook.createSheet();
        Font font = getDefaultFont(workbook);
        CellStyle thStyle = getTableHeaderStyle(workbook, font);
        CellStyle style = getDefaultStyle(workbook, font);
        CellStyle sumStyle = getSumStyle(workbook, font);

        setColumnsWidth(sheet, reportWriter.getColumnsWidth());
        makeHeader(sheet.createRow(0), thStyle, lang, reportWriter.getColumnNames());

        int rowIndex = 1;
        for (int index = 0; index < objects.size(); index++) {
            Row row = sheet.createRow(rowIndex++);
            row.setRowStyle(style);
            fillRow(row, reportWriter.getColumnValues(index));
        }

        Object[] sumValues = reportWriter.getSumValues();
        if (CollectionUtils.isNotEmpty(sumValues)) {
            Row row = sheet.createRow(rowIndex++);
            fillRow(row, sumValues, sumStyle);
        }

        workbook.write(outputStream);
        workbook.dispose();
    }

    public static void writeIssuesReport(List<CaseObject> issues, List<List<CaseComment>> issuesComments, OutputStream outputStream, Lang.LocalizedLang lang) throws IOException {
        final DateFormat dateFormat = getDateFormat();
        if (issues.size() != issuesComments.size()) {
            throw new IllegalArgumentException("Not equal size of issues and their comments");
        }
        WorkTimeFormatter workTimeFormatter = new WorkTimeFormatter();
        writeReport(issues, outputStream, lang, new ReportWriter() {
            @Override
            public int[] getColumnsWidth() {
                return new int[] {
                        3650,
                        3430,
                        8570,
                        4590,
                        4200,
                        4200,
                        6000,
                        3350,
                        4600,
                        4200,
                        5800,
                        5800,
                        5800,
                        5800,
                        5800,
                        5800,
                        5800,
                        5800
                };
            }
            @Override
            public String[] getColumnNames() {
                return new String[] {
                        "ir_caseno",
                        "ir_private",
                        "ir_name",
                        "ir_company",
                        "ir_initiator",
                        "ir_manager",
                        "ir_product",
                        "ir_importance",
                        "ir_state",
                        "ir_date_created",
                        "ir_date_opened",
                        "ir_date_workaround",
                        "ir_date_customer_test",
                        "ir_date_done",
                        "ir_date_verify",
                        "ir_time_solution_first",
                        "ir_time_solution_full",
                        "ir_time_elapsed"
                };
            }
            @Override
            public Object[] getColumnValues(int index) {

                CaseObject issue = issues.get(index);
                List<CaseComment> comments = issuesComments.get(index);
                Date    created = null,
                        opened = null,
                        workaround = null,
                        customerTest = null,
                        done = null,
                        verified = null;
                for (CaseComment comment : comments) {
                    En_CaseState state = En_CaseState.getById(comment.getCaseStateId());
                    if (state == null) {
                        continue;
                    }
                    switch (state) {
                        case CREATED: created = comment.getCreated(); break;
                        case OPENED: opened = comment.getCreated(); break;
                        case WORKAROUND: workaround = comment.getCreated(); break;
                        case TEST_CUST: customerTest = comment.getCreated(); break;
                        case DONE: done = comment.getCreated(); break;
                        case VERIFIED: verified = comment.getCreated(); break;
                    }
                }
                if (created == null) {
                    created = issue.getCreated();
                }
                Long solutionDurationFirst = getDurationBetween(created, customerTest, workaround, done);
                Long solutionDurationFull = getDurationBetween(created, done, verified);

                return new Object[] {
                        "CRM-" + issue.getCaseNumber(),
                        lang.get(issue.isPrivateCase() ? "yes" : "no"),
                        HelperFunc.isNotEmpty(issue.getName()) ? issue.getName() : "",
                        issue.getInitiatorCompany() != null && HelperFunc.isNotEmpty(issue.getInitiatorCompany().getCname()) ? issue.getInitiatorCompany().getCname() : "",
                        issue.getInitiator() != null && HelperFunc.isNotEmpty(issue.getInitiator().getDisplayShortName()) ? issue.getInitiator().getDisplayShortName() : "",
                        issue.getManager() != null && HelperFunc.isNotEmpty(issue.getManager().getDisplayShortName()) ? issue.getManager().getDisplayShortName() : "",
                        issue.getProduct() != null && HelperFunc.isNotEmpty(issue.getProduct().getName()) ? issue.getProduct().getName() : "",
                        issue.getImpLevel() != null ? lang.get("importance_" + String.valueOf(issue.getImpLevel())) : "",
                        issue.getState() != null ? lang.get("case_state_" + String.valueOf(issue.getState().getId())) : "",
                        created != null ? dateFormat.format(created) : "",
                        opened != null ? dateFormat.format(opened) : "",
                        workaround != null ? dateFormat.format(workaround) : "",
                        customerTest != null ? dateFormat.format(customerTest) : "",
                        done != null ? dateFormat.format(done) : "",
                        verified != null ? dateFormat.format(verified) : "",
                        solutionDurationFirst != null ? duration2string(solutionDurationFirst, lang) : "",
                        solutionDurationFull != null ? duration2string(solutionDurationFull, lang) : "",
                        issue.getTimeElapsed() != null && issue.getTimeElapsed() > 0 ?
                                workTimeFormatter.format(issue.getTimeElapsed(), lang.get("timeDayLiteral"), lang.get("timeHourLiteral"), lang.get("timeMinuteLiteral"))
                                : ""
                };
            }
            @Override
            public Object[] getSumValues() {
                return null;
            }
        });
    }

    private static Long getDurationBetween(Date from, Date... toList) {
        if (toList == null || from == null) {
            return null;
        }
        Date to = null;
        for (Date t : toList) {
            if (t != null) {
                to = t;
                break;
            }
        }
        if (to != null) {
            for (Date t : toList) {
                if (t != null && t.after(to)) {
                    to = t;
                }
            }
            Long minutes = to.getTime() / 60000L - from.getTime() / 60000L;
            return minutes > 0 ? minutes : null;
        }
        return null;
    }

    private static String duration2string(Long minutes, Lang.LocalizedLang lang) {
        StringBuilder sb = new StringBuilder();
        long days = minutes / (60 * 24);
        minutes = minutes % (60 * 24);
        long hours = minutes / 60;
        minutes = minutes % 60;
        if (days > 0) {
            sb.append(days);
            sb.append(" ");
            sb.append(lang.get("days"));
            if (hours > 0 || minutes > 0) {
                sb.append(", ");
            }
        }
        if (hours > 0 || minutes > 0) {
            sb.append(String.format("%02d:%02d", hours, minutes));
        }
        return sb.toString();
    }
}
