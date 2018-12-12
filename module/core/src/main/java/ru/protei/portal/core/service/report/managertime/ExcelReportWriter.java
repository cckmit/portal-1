package ru.protei.portal.core.service.report.managertime;

import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseCommentCaseObject;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.service.report.ReportWriter;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.List;

public class ExcelReportWriter implements
        ReportWriter<CaseCommentCaseObject>,
        JXLSHelper.ReportBook.Writer<CaseCommentCaseObject> {

    private final JXLSHelper.ReportBook<CaseCommentCaseObject> book;
    private final Lang.LocalizedLang lang;
    private final DateFormat dateFormat;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, DateFormat dateFormat) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.dateFormat = dateFormat;
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
    public void write(int sheetNumber, List<CaseCommentCaseObject> objects) {
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
                3650, 3430, 8570,
                4590, 4200, 4200,
                3350, 4600, 4200,
                5800
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {
                "ir_caseno", "ir_private", "ir_name",
                "ir_company", "ir_performer", "ir_manager",
                "ir_importance", "ir_state", "ir_date_created",
                "ir_actual_work_time"
        };
    }

    @Override
    public Object[] getColumnValues(CaseCommentCaseObject object) {
        CaseObject caseObject = object.getCaseObject();
        CaseComment caseComment = object.getCaseComment();
        return new Object[] {
                "CRM-" + caseObject.getCaseNumber(),
                lang.get(caseObject.isPrivateCase() ? "yes" : "no"),
                HelperFunc.isNotEmpty(caseObject.getName()) ? caseObject.getName() : "",
                caseObject.getInitiatorCompany() != null && HelperFunc.isNotEmpty(caseObject.getInitiatorCompany().getCname()) ?
                        caseObject.getInitiatorCompany().getCname() : "",
                caseComment.getAuthor() != null && HelperFunc.isNotEmpty(caseComment.getAuthor().getDisplayName()) ?
                        caseComment.getAuthor().getDisplayName() : "",
                caseObject.getManager() != null && HelperFunc.isNotEmpty(caseObject.getManager().getDisplayShortName()) ?
                        caseObject.getManager().getDisplayShortName() : "",
                caseObject.getImpLevel() != null ? lang.get("importance_" + caseObject.getImpLevel()) : "",
                caseObject.getState() != null ? lang.get("case_state_" + caseObject.getState().getId()) : "",
                caseObject.getCreated() != null ? dateFormat.format(caseObject.getCreated()) : "",
                ""
        };
    }
}
