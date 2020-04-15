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
import java.util.Locale;

import static ru.protei.portal.core.model.util.TransliterationUtils.transliterate;

public class ExcelReportWriter implements
        ReportWriter<CaseCommentTimeElapsedSum>,
        JXLSHelper.ReportBook.Writer<CaseCommentTimeElapsedSum> {

    private final JXLSHelper.ReportBook<CaseCommentTimeElapsedSum> book;
    private final Lang.LocalizedLang lang;
    private final DateFormat dateFormat;
    private final TimeFormatter timeFormatter;
    private final Locale locale;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, DateFormat dateFormat, TimeFormatter timeFormatter) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.dateFormat = dateFormat;
        this.timeFormatter = timeFormatter;
        this.locale = localizedLang.getLocale();
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
                4590, 4200, 4200, 4200,
                3350, 4600, 4200,
                5800, 5800, 5800, 5800
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {
                "ir_caseno", "ir_private", "ir_name",
                "ir_company", "ir_product", "ir_performer", "ir_manager",
                "ir_importance", "ir_state", "ir_date_created",
                "ir_work_time_none", "ir_work_time_watch", "ir_work_time_night_work",
                "ir_work_time_SoftInstall", "ir_work_time_SoftUpdate", "ir_work_time_SoftConfig",
                "ir_work_time_Testing", "ir_work_time_Consultation", "ir_work_time_Meeting",
                "ir_work_time_DiscussionOfImprovements", "ir_work_time_LogAnalysis", "ir_work_time_SolveProblems", "ir_work_time_all"
        };
    }

    @Override
    public Object[] getColumnValues(CaseCommentTimeElapsedSum object) {
        if (object.getAuthorDisplayName() == null) {
            // summary
            return new Object[] {
                    "", "", "",
                    "", "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", lang.get("summary") + ":", timeFormatter.formatHourMinutes(object.getTimeElapsedSum())
            };
        }
        return new Object[] {
                "CRM-" + object.getCaseNumber(),
                lang.get(object.isCasePrivateCase() ? "yes" : "no"),
                HelperFunc.isNotEmpty(object.getCaseName()) ? object.getCaseName() : "",
                HelperFunc.isNotEmpty(object.getCaseCompanyName()) ? transliterate(object.getCaseCompanyName(), locale) : "",
                HelperFunc.isNotEmpty(object.getProductName()) ? object.getProductName() : "",
                HelperFunc.isNotEmpty(object.getAuthorDisplayName()) ? transliterate(object.getAuthorDisplayName(), locale) : "",
                HelperFunc.isNotEmpty(object.getCaseManagerDisplayName()) ? transliterate(object.getCaseManagerDisplayName(), locale) : "",
                object.getCaseImpLevel() != null ? lang.get("importance_" + object.getCaseImpLevel()) : "",
                object.getCaseState() != null ? object.getCaseState().getName() : "",
                object.getCaseCreated() != null ? dateFormat.format(object.getCaseCreated()) : "",
                timeFormatter.formatHourMinutes(object.getTimeElapsedNone()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedWatch()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedNightWork()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedTypeSoftInstall()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedTypeSoftUpdate()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedTypeSoftConfig()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedTypeTesting()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedTypeConsultation()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedTypeMeeting()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedTypeDiscussionOfImprovements()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedTypeLogAnalysis()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedTypeSolveProblems()),
                timeFormatter.formatHourMinutes(object.getTimeElapsedSum())
        };
    }
}
