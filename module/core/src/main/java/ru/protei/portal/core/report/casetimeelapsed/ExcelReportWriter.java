package ru.protei.portal.core.report.casetimeelapsed;

import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.JXLSHelper;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.List;

public class ExcelReportWriter implements
        ReportWriter<CaseCommentTimeElapsedSum>,
        JXLSHelper.ReportBook.Writer<CaseCommentTimeElapsedSum> {

    private final JXLSHelper.ReportBook<CaseCommentTimeElapsedSum> book;
    private final Lang.LocalizedLang lang;
    private final DateFormat dateFormat;
    private final TimeFormatter timeFormatter;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, DateFormat dateFormat, TimeFormatter timeFormatter) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.dateFormat = dateFormat;
        this.timeFormatter = timeFormatter;
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
    public void write(int sheetNumber, List<CaseCommentTimeElapsedSum> objects) {
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
    public Object[] getColumnValues(CaseCommentTimeElapsedSum object) {
        if (object.getAuthorDisplayName() == null) {
            // summary
            return new Object[] {
                    "", "", "",
                    "", "", "",
                    "", "", lang.get("summary") + ":",
                    timeFormatter.formatHourMinutes(object.getTimeElapsedSum())
            };
        }
        return new Object[] {
                "CRM-" + object.getCaseNumber(),
                lang.get(object.isCasePrivateCase() ? "yes" : "no"),
                HelperFunc.isNotEmpty(object.getCaseName()) ? object.getCaseName() : "",
                HelperFunc.isNotEmpty(object.getCaseCompanyName()) ? object.getCaseCompanyName() : "",
                HelperFunc.isNotEmpty(object.getAuthorDisplayName()) ? object.getAuthorDisplayName() : "",
                HelperFunc.isNotEmpty(object.getCaseManagerDisplayName()) ? object.getCaseManagerDisplayName() : "",
                object.getCaseImpLevel() != null ? lang.get("importance_" + object.getCaseImpLevel()) : "",
                object.getCaseState() != null ? lang.get("case_state_" + object.getCaseState().getId()) : "",
                object.getCaseCreated() != null ? dateFormat.format(object.getCaseCreated()) : "",
                timeFormatter.formatHourMinutes(object.getTimeElapsedSum())
        };
    }
}
