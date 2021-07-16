package ru.protei.portal.core.report.ytwork;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_ReportYtWorkType;
import ru.protei.portal.core.model.struct.ListBuilder;
import ru.protei.portal.core.model.struct.ReportYtWorkItem;
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
        ReportWriter<ReportYtWorkItem>,
        JXLSHelper.ReportBook.Writer<ReportYtWorkItem> {

    private final JXLSHelper.ReportBook<ReportYtWorkItem> book;
    private final String[] formats;
    private final Map<En_ReportYtWorkType, Set<String>> processedWorkTypes;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, Map<En_ReportYtWorkType, Set<String>> processedWorkTypes) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.formats = getFormats();
        this.processedWorkTypes = processedWorkTypes;
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
    public void write(int sheetNumber, List<ReportYtWorkItem> objects) {
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
                .add(5800);
        processedWorkTypes.forEach((ytWorkType, strings) -> {
            strings.forEach(value -> columnsWidthList.add(5800));
        });
        return toPrimitiveIntegerArray(columnsWidthList.build());
    }

    @Override
    public String[] getLangColumnNames() {
        return new String[]{"reportYtWorkPersonName"};
    }
    @Override
    public String[] getColumnNames() {
        ListBuilder<String> columnsList = new ListBuilder<>();
        int count = 0;
        for (Map.Entry<En_ReportYtWorkType, Set<String>> entry : processedWorkTypes.entrySet()) {
            entry.getValue().forEach(value -> columnsList.add(entry.getKey() + " : " + value));
            count =+ entry.getValue().size();
        }
        String[] array = new String[count];
        return columnsList.build().toArray(array);
    }

    @Override
    public Object[] getColumnValues(ReportYtWorkItem item) {
        List<Object> values = new ArrayList<>();

        values.add(item.getPerson().getDisplayShortName());

        processedWorkTypes.forEach((ytWorkType, strings) -> {
            strings.forEach(value -> {
                Long fieldValue = 0L;
                switch (ytWorkType) {
                    case NIOKR: fieldValue = item.getNiokrSpentTime().getOrDefault(value,0L); break;
                    case NMA: fieldValue = item.getNmaSpentTime().getOrDefault(value,0L); break;
                    case CONTRACT: fieldValue = item.getContractSpentTime().getOrDefault(value,0L); break;
                    case GUARANTEE: fieldValue = item.getGuaranteeSpentTime().getOrDefault(value,0L); break;
                }
                values.add(fieldValue);
            });
        });

        return values.toArray();
    }

    private String[] getFormats() {
        ListBuilder<String> columnsList = new ListBuilder<String>()
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
