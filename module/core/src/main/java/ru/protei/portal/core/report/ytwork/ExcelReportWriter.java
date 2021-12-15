package ru.protei.portal.core.report.ytwork;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.struct.ListBuilder;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkClassificationError;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRow;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowHeader;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.ExcelFormatUtils.ExcelFormat;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.toPrimitiveIntegerArray;

public class ExcelReportWriter implements
        ReportWriter<ReportYtWorkRow>,
        JXLSHelper.ReportBook.Writer<ReportYtWorkRow> {

    private final Lang.LocalizedLang localizedLang;
    private final JXLSHelper.ReportBook<ReportYtWorkRow> book;
    private Map<En_YoutrackWorkType, Set<String>> processedWorkTypes = new HashMap<>();

    public ExcelReportWriter(Lang.LocalizedLang localizedLang) {
        this.localizedLang = localizedLang;
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
    }

    @Override
    public int createSheet() {
        return book.createSheet();
    }

    @Override
    public void setSheetName(int sheetNumber, String name) {
        book.setSheetName(sheetNumber, name);
    }

    @Override
    public void write(int sheetNumber, List<ReportYtWorkRow> objects) {
        book.write(sheetNumber, objects);
    }

    @Override
    public void collect(OutputStream outputStream) throws IOException {
        book.collect(outputStream);
    }

    @Override
    public void close() throws IOException {
        book.close();
    }

    @Override
    public CellStyle getCellStyle(Workbook workbook, int columnIndex) {
        String[] formats = currentSheet.getFormats();
        return book.makeCellStyle(columnIndex, cs -> {
            cs.setFont(book.getDefaultFont());
            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            cs.setDataFormat(workbook.createDataFormat()
                    .getFormat(formats[columnIndex]));
        });
    }

    @Override
    public int[] getColumnsWidth() {
        return currentSheet.getColumnsWidth();
    }

    @Override
    public String[] getLangColumnNames() {
        return currentSheet.getLangColumnNames();
    }

    @Override
    public String[] getColumnNames() {
        return currentSheet.getColumnNames();
    }

    @Override
    public Object[] getColumnValues(ReportYtWorkRow row) {
        return currentSheet.getColumnValues(row);
    }

    public void setClassificationErrorSheet() {
        currentSheet = classificationErrorSheet;
    }

    public void setValueSheet(Map<En_YoutrackWorkType, Set<String>> processedWorkTypes) {
        this.processedWorkTypes = processedWorkTypes;
        currentSheet = valueSheet;
    }

    private final Sheet classificationErrorSheet = new ClassificationErrorSheet();
    private final Sheet valueSheet = new ValueSheet();
    private Sheet currentSheet = classificationErrorSheet;

    private interface Sheet {
        int[] getColumnsWidth();
        String[] getLangColumnNames();
        default String[] getColumnNames() { return new String[0];}
        Object[] getColumnValues(ReportYtWorkRow row);
        String[] getFormats();
    }

    private class ValueSheet implements Sheet {
        private final Map<Integer, String> levelMark;

        public ValueSheet() {
            levelMark = new HashMap<>();
            levelMark.put(0, "== ");
            levelMark.put(1, "==== ");
            levelMark.put(2, "====== ");
            levelMark.put(3, "======== ");
        }

        @Override
        public int[] getColumnsWidth() {
            ListBuilder<Integer> columnsWidthList = new ListBuilder<Integer>()
                    .add(5800).add(5800).add(5800).add(5800).add(5800);
            processedWorkTypes.forEach((ytWorkType, strings) ->
                    strings.forEach(value -> columnsWidthList.add(5800)));
            return toPrimitiveIntegerArray(columnsWidthList.build());
        }

        @Override
        public String[] getLangColumnNames() {
            return new String[]{"reportYtWorkPersonName",
                    "reportYtWorkAllSpentTimeInMinutes", "reportYtWorkAllSpentTimeFormatted",
                    "reportYtWorkHomeCompanySpentTime", "reportYtWorkWorkHours"};
        }

        @Override
        public String[] getColumnNames() {
            ListBuilder<String> columnsList = new ListBuilder<>();
            int count = 0;
            for (Map.Entry<En_YoutrackWorkType, Set<String>> entry : processedWorkTypes.entrySet()) {
                entry.getValue().forEach(value -> columnsList.add(localizedLang.get("reportYtWorkType" + entry.getKey()) + ": " + value));
                count += entry.getValue().size();
            }
            String[] array = new String[count];
            return columnsList.build().toArray(array);
        }

        @Override
        public Object[] getColumnValues(ReportYtWorkRow row) {
            List<Object> values = new ArrayList<>();

            if (row instanceof ReportYtWorkRowItem) {
                ReportYtWorkRowItem item = (ReportYtWorkRowItem)row;

                values.add(item.getPersonInfo().getDisplayName() == null? localizedLang.get("reportYtWorkPersonNoName") : item.getPersonInfo().getDisplayName());
                long allTimeSpent = item.getAllTimeSpent();
                values.add(allTimeSpent);
                values.add(localizedLang.get("reportYtWorkRepresentTime", new Object[]{allTimeSpent / 60, allTimeSpent % 60}));
                values.add(String.valueOf(item.getHomeCompanySpentTime()));
                values.add(item.getWorkedHours().map(String::valueOf).orElseGet(() -> localizedLang.get("reportYtWorkWorkHoursNoData")));
                processedWorkTypes.forEach((ytWorkType, strings) ->
                        strings.forEach(value -> values.add(item.selectSpentTimeMap(ytWorkType).getOrDefault(value, 0L))));
            }
            if (row instanceof ReportYtWorkRowHeader) {
                ReportYtWorkRowHeader header = (ReportYtWorkRowHeader)row;
                values.add(levelMark.getOrDefault(header.getLevel(), "?") + header.getValue());
            }

            return values.toArray();
        }

        @Override
        public String[] getFormats() {
            ListBuilder<String> columnsList = new ListBuilder<String>()
                    .add(ExcelFormat.STANDARD)
                    .add(ExcelFormat.STANDARD)
                    .add(ExcelFormat.STANDARD)
                    .add(ExcelFormat.STANDARD)
                    .add(ExcelFormat.STANDARD);
            int count = 0;
            for (Map.Entry<En_YoutrackWorkType, Set<String>> entry : processedWorkTypes.entrySet()) {
                entry.getValue().forEach(value -> columnsList.add(ExcelFormat.STANDARD));
                count += entry.getValue().size();
            }
            String[] array = new String[count];
            return columnsList.build().toArray(array);
        }
    }

    static private class ClassificationErrorSheet implements Sheet {
        @Override
        public int[] getColumnsWidth() {
            return new int[]{5800};
        }

        @Override
        public String[] getLangColumnNames() {
            return new String[]{"reportYtWorkClassificationError"};
        }

        @Override
        public Object[] getColumnValues(ReportYtWorkRow row) {
            List<Object> values = new ArrayList<>();

            if (row instanceof ReportYtWorkClassificationError) {
                ReportYtWorkClassificationError error = (ReportYtWorkClassificationError)row;
                values.add(error.getIssue());
            }

            return values.toArray();
        }

        @Override
        public String[] getFormats() {
            return new String[]{ExcelFormat.STANDARD};
        }
    }
}
