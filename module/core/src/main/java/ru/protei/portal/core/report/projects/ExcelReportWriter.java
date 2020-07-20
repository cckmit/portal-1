package ru.protei.portal.core.report.projects;

import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.ReportProjectWithLastComment;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReportWriter implements
        ReportWriter<ReportProjectWithLastComment>,
        JXLSHelper.ReportBook.Writer<ReportProjectWithLastComment> {

    private final JXLSHelper.ReportBook<ReportProjectWithLastComment> book;
    private final Lang.LocalizedLang lang;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
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
    public int[] getColumnsWidth() {
        return new int[] {
                2350, 12570, 4200,
                5200, 5800, 5800,
                3800, 18570,
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {
                "ir_id", "ir_name", "ir_state",
                "ir_customerType", "ir_customer", "ir_region",
                "ir_direction", "ir_last_comment_text",
        };
    }

    @Override
    public Object[] getColumnValues(ReportProjectWithLastComment object) {
        Project project = object.getProject();
        CaseComment comment = object.getLastComment();

        List<Object> values = new ArrayList<>();

        values.add(project.getId());
        values.add(HelperFunc.isNotEmpty(project.getName()) ? project.getName() : "");
        values.add(project.getState() != null ? lang.get("regionState_" + project.getState()) : "");
        values.add(project.getCustomerType() != null ? lang.get("customerType_" + project.getCustomerType()) : "");
        values.add(project.getCustomer() != null ? project.getCustomer().getCname() : "");
        values.add(project.getRegion() != null && project.getRegion().getDisplayText() != null ?
                project.getRegion().getDisplayText() : "");
        values.add(project.getProductDirection() != null && project.getProductDirection().getDisplayText() != null ?
                project.getProductDirection().getDisplayText() : "");
        values.add(comment != null ? comment.getText() : "");

        return values.toArray();
    }
}
