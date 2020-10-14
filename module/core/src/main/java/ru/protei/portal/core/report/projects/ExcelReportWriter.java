package ru.protei.portal.core.report.projects;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.ReportProjectWithLastComment;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.ExcelFormatUtils.ExcelFormat;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelReportWriter implements
        ReportWriter<ReportProjectWithLastComment>,
        JXLSHelper.ReportBook.Writer<ReportProjectWithLastComment> {

    private final JXLSHelper.ReportBook<ReportProjectWithLastComment> book;
    private final Lang.LocalizedLang lang;
    private final EnumLangUtil enumLangUtil;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, EnumLangUtil enumLangUtil) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.enumLangUtil = enumLangUtil;
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
    public void write(int sheetNumber, List<ReportProjectWithLastComment> objects) {
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
                    .getFormat(getFormats()[columnIndex]));
        });
    }

    private String[] getFormats() {
        return new String[] {
                ExcelFormat.STANDARD, ExcelFormat.STANDARD, ExcelFormat.STANDARD,
                ExcelFormat.STANDARD, ExcelFormat.STANDARD, ExcelFormat.STANDARD,
                ExcelFormat.STANDARD, ExcelFormat.FULL_DATE, ExcelFormat.FULL_DATE_TIME, ExcelFormat.STANDARD
        };
    }

    @Override
    public int[] getColumnsWidth() {
        return new int[] {
                2350, 12570, 4200,
                5200, 5800, 5800,
                3800, 6800, 5800, 18570
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {
                "ir_id", "ir_name", "ir_state",
                "ir_customerType", "ir_customer", "ir_region",
                "ir_direction", "ir_pause_date", "ir_last_comment_date", "ir_last_comment_text"
        };
    }

    @Override
    public Object[] getColumnValues(ReportProjectWithLastComment object) {
        Project project = object.getProject();
        CaseComment comment = object.getLastComment();

        List<Object> values = new ArrayList<>();

        values.add(project.getId());
        values.add(HelperFunc.isNotEmpty(project.getName()) ? project.getName() : "");
        values.add(project.getState() != null ? enumLangUtil.getRegionState(project.getState(), lang.getLanguageTag()) : "");
        values.add(project.getCustomerType() != null ? enumLangUtil.getCustomerType(project.getCustomerType(), lang.getLanguageTag()) : "");
        values.add(project.getCustomer() != null ? project.getCustomer().getCname() : "");
        values.add(project.getRegion() != null && project.getRegion().getDisplayText() != null ?
                project.getRegion().getDisplayText() : "");
        values.add(project.getProductDirectionEntityOption() != null && project.getProductDirectionEntityOption().getDisplayText() != null ?
                project.getProductDirectionEntityOption().getDisplayText() : "");
        values.add(project.getPauseDate() != null ? new Date(project.getPauseDate()) : "");
        values.add(comment != null ? comment.getCreated() : "");
        values.add(comment != null ? comment.getText() : "");

        return values.toArray();
    }
}
