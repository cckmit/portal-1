package ru.protei.portal.core.report.ytwork;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.ListBuilder;
import ru.protei.portal.core.model.struct.ReportYtWorkItem;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.ExcelFormatUtils.ExcelFormat;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;
import static ru.protei.portal.core.model.helper.CollectionUtils.toPrimitiveIntegerArray;

public class ExcelReportWriter implements
        ReportWriter<ReportYtWorkItem>,
        JXLSHelper.ReportBook.Writer<ReportYtWorkItem> {

    private final JXLSHelper.ReportBook<ReportYtWorkItem> book;
    private final Lang.LocalizedLang lang;
    private final EnumLangUtil enumLangUtil;
    private final String[] formats;
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, EnumLangUtil enumLangUtil) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.enumLangUtil = enumLangUtil;
        this.formats = getFormats();
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
        return getColumnsWidth(false);
    }

    private int[] getColumnsWidth(boolean withComments) {
        List<Integer> columnsWidthList = new ListBuilder<Integer>()
                .add(2350).add(12570).add(4200)
                .add(5200).add(5800).add(5800)
                .add(3800).add(6800).add(5800).add(18570)
                .addIf(18570, withComments)
                .build();

        return toPrimitiveIntegerArray(columnsWidthList);
    }

    @Override
    public String[] getColumnNames() {
        return getColumns(false);
    }

    private String[] getColumns(boolean withComments) {
        List<String> columnsList = new ListBuilder<String>()
                .add("ir_id").add("ir_name").add("ir_state")
                .add("ir_customerType").add("ir_customer").add("ir_region")
                .add("ir_direction").add("ir_pause_date").add("ir_last_comment_date").add("ir_last_comment_text")
                .addIf("ir_comments_for_period", withComments)
                .build();

        return columnsList.toArray(new String[]{});
    }

    @Override
    public Object[] getColumnValues(ReportYtWorkItem item) {
        Project project = null;
        CaseComment lastComment = null;

        List<Object> values = new ArrayList<>();

        values.add(project.getId());
        values.add(HelperFunc.isNotEmpty(project.getName()) ? project.getName() : "");
        values.add(project.getStateName() != null ? enumLangUtil.getProjectState(project.getStateName(), lang.getLanguageTag()) : "");
        values.add(project.getCustomerType() != null ? enumLangUtil.getCustomerType(project.getCustomerType(), lang.getLanguageTag()) : "");
        values.add(project.getCustomer() != null ? project.getCustomer().getCname() : "");
        values.add(project.getRegion() != null && project.getRegion().getDisplayText() != null ?
                project.getRegion().getDisplayText() : "");
        values.add(joining(project.getProductDirectionEntityOptionList(), ", ", EntityOption::getDisplayText));
        values.add(project.getPauseDate() != null ? new Date(project.getPauseDate()) : "");
        values.add(lastComment != null ? lastComment.getCreated() : "");
        values.add(lastComment != null ? lastComment.getText() : "");

        return values.toArray();
    }

    private String[] getFormats() {
        return new String[] {
                ExcelFormat.STANDARD, ExcelFormat.STANDARD, ExcelFormat.STANDARD,
                ExcelFormat.STANDARD, ExcelFormat.STANDARD, ExcelFormat.STANDARD,
                ExcelFormat.STANDARD, ExcelFormat.FULL_DATE, ExcelFormat.FULL_DATE_TIME, ExcelFormat.STANDARD,
                ExcelFormat.STANDARD
        };
    }
}
