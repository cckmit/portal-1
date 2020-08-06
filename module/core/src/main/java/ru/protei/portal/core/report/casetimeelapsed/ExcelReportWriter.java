package ru.protei.portal.core.report.casetimeelapsed;

import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.JXLSHelper;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.List;

import static java.time.Duration.ofMinutes;
import static java.util.Optional.ofNullable;
import static org.apache.poi.ss.usermodel.DateUtil.SECONDS_PER_DAY;
import static ru.protei.portal.core.model.util.TransliterationUtils.transliterate;
import static ru.protei.portal.core.report.casetimeelapsed.ExcelReportWriter.ExcelFormat.*;

public class ExcelReportWriter implements
        ReportWriter<CaseCommentTimeElapsedSum>,
        JXLSHelper.ReportBook.Writer<CaseCommentTimeElapsedSum> {

    private final JXLSHelper.ReportBook<CaseCommentTimeElapsedSum> book;
    private final Lang.LocalizedLang lang;
    private final DateFormat dateFormat;
    private final TimeFormatter timeFormatter;
    private final String locale;

    public interface ExcelFormat {
        String STANDARD = "@";
        String DATE_TIME = "DD.MM.YY HH:MM";
        String INFINITE_HOURS_MINUTES = "[H]:MM";
    }

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, DateFormat dateFormat, TimeFormatter timeFormatter) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.dateFormat = dateFormat;
        this.timeFormatter = timeFormatter;
        this.locale = localizedLang.getLanguageTag();
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
                3350, 4600, 5800, 4200,
                5800, 5800, 5800, 5800
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {
                "ir_caseno", "ir_private", "ir_name",
                "ir_company", "ir_product", "ir_performer", "ir_manager",
                "ir_importance", "ir_state", "ir_tags", "ir_date_created",
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
                    "", "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", lang.get("summary") + ":", (double) ofMinutes( ofNullable(object.getTimeElapsedSum()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY
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
                object.getImportanceLevel() != null ? object.getImportanceLevel().getCode() : "",
                HelperFunc.isNotEmpty(object.getCaseStateName()) ? object.getCaseStateName() : "",
                StringUtils.emptyIfNull(object.getTags()),
                object.getCaseCreated() != null ? object.getCaseCreated() : "",
                (double) ofMinutes( ofNullable(object.getTimeElapsedNone()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedWatch()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedNightWork()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedTypeSoftInstall()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedTypeSoftUpdate()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedTypeSoftConfig()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedTypeTesting()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedTypeConsultation()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedTypeMeeting()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedTypeDiscussionOfImprovements()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedTypeLogAnalysis()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedTypeSolveProblems()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
                (double) ofMinutes( ofNullable(object.getTimeElapsedSum()).orElse(0L) ).getSeconds() / SECONDS_PER_DAY,
        };
    }

    @Override
    public String[] getFormats() {
        return new String[]{
                STANDARD, STANDARD, STANDARD,
                STANDARD, STANDARD, STANDARD, STANDARD,
                STANDARD, STANDARD, STANDARD, DATE_TIME,
                INFINITE_HOURS_MINUTES, INFINITE_HOURS_MINUTES, INFINITE_HOURS_MINUTES,
                INFINITE_HOURS_MINUTES, INFINITE_HOURS_MINUTES, INFINITE_HOURS_MINUTES,
                INFINITE_HOURS_MINUTES, INFINITE_HOURS_MINUTES, INFINITE_HOURS_MINUTES,
                INFINITE_HOURS_MINUTES, INFINITE_HOURS_MINUTES, INFINITE_HOURS_MINUTES, INFINITE_HOURS_MINUTES
        };
    }
}
