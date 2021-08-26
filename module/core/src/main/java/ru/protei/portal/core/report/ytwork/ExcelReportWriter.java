package ru.protei.portal.core.report.ytwork;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_ReportYtWorkType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.toPrimitiveIntegerArray;

public class ExcelReportWriter implements
        ReportWriter<ReportYtWorkRow>,
        JXLSHelper.ReportBook.Writer<ReportYtWorkRow> {

    private final Lang.LocalizedLang localizedLang;
    private final JXLSHelper.ReportBook<ReportYtWorkRow> book;
    private final String[] formats;
    private final Map<En_ReportYtWorkType, Set<String>> processedWorkTypes;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, Map<En_ReportYtWorkType, Set<String>> processedWorkTypes) {
        this.localizedLang = localizedLang;
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.processedWorkTypes = processedWorkTypes;
        this.formats = getFormats(processedWorkTypes);
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
        return book.makeCellStyle(columnIndex, cs -> {
            cs.setFont(book.getDefaultFont());
            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            cs.setDataFormat(workbook.createDataFormat()
                    .getFormat(formats[columnIndex]));
        });
    }

    @Override
    public int[] getColumnsWidth() {
        return getColumnsWidth(processedWorkTypes);
    }

    private int[] getColumnsWidth(Map<En_ReportYtWorkType, Set<String>> processedWorkTypes) {
        ListBuilder<Integer> columnsWidthList = new ListBuilder<Integer>()
                .add(5800).add(5800).add(5800).add(5800);
        processedWorkTypes.forEach((ytWorkType, strings) ->
                strings.forEach(value -> columnsWidthList.add(5800)));
        return toPrimitiveIntegerArray(columnsWidthList.build());
    }

    @Override
    public String[] getLangColumnNames() {
        return new String[]{"reportYtWorkPersonName",
                "reportYtWorkAllSpentTimeInMinutes", "reportYtWorkAllSpentTimeFormatted",
                "reportYtWorkWorkHours"};
    }
    @Override
    public String[] getColumnNames() {
        ListBuilder<String> columnsList = new ListBuilder<>();
        int count = 0;
        for (Map.Entry<En_ReportYtWorkType, Set<String>> entry : processedWorkTypes.entrySet()) {
            entry.getValue().forEach(value -> columnsList.add(entry.getKey() + " : " + value));
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

            values.add(item.getPersonInfo().getDisplayName());
            long allTimeSpent = item.getAllTimeSpent();
            values.add(allTimeSpent);
            values.add(localizedLang.get("reportYtWorkRepresentTime", new Object[]{allTimeSpent / 60, allTimeSpent % 60}));
            values.add(item.getWorkedHours());
            processedWorkTypes.forEach((ytWorkType, strings) ->
                    strings.forEach(value -> values.add(item.selectSpentTimeMap(ytWorkType).getOrDefault(value, 0L))));
        }
        if (row instanceof ReportYtWorkRowHeader) {
            ReportYtWorkRowHeader header = (ReportYtWorkRowHeader)row;
            values.add(header.getValue());
        }
        if (row instanceof ReportYtWorkClassificationError) {
            ReportYtWorkClassificationError error = (ReportYtWorkClassificationError)row;
            values.add(error.getIssue());
        }

        return values.toArray();
    }

    private String[] getFormats(Map<En_ReportYtWorkType, Set<String>> processedWorkTypes) {
        ListBuilder<String> columnsList = new ListBuilder<String>()
                .add(ExcelFormat.STANDARD)
                .add(ExcelFormat.STANDARD)
                .add(ExcelFormat.STANDARD)
                .add(ExcelFormat.STANDARD);
        int count = 0;
        for (Map.Entry<En_ReportYtWorkType, Set<String>> entry : processedWorkTypes.entrySet()) {
            entry.getValue().forEach(value -> columnsList.add(ExcelFormat.STANDARD));
            count =+ entry.getValue().size();
        }
        String[] array = new String[count];
        return columnsList.build().toArray(array);
    }
}
