package ru.protei.portal.core.report.casetimeelapsed;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.ExcelFormatUtils.ExcelFormat;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static ru.protei.portal.core.model.util.TransliterationUtils.transliterate;
import static ru.protei.portal.core.utils.ExcelFormatUtils.toExcelTimeFormat;

public class ExcelReportWriter implements
        ReportWriter<CaseCommentTimeElapsedSum>,
        JXLSHelper.ReportBook.Writer<CaseCommentTimeElapsedSum> {

    private final JXLSHelper.ReportBook<CaseCommentTimeElapsedSum> book;
    private final Lang.LocalizedLang lang;
    private final String locale;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
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
                ExcelFormat.STANDARD, ExcelFormat.STANDARD, ExcelFormat.STANDARD, ExcelFormat.STANDARD,
                ExcelFormat.STANDARD, ExcelFormat.STANDARD, ExcelFormat.FULL_DATE_TIME,
                ExcelFormat.INFINITE_HOURS_MINUTES, ExcelFormat.INFINITE_HOURS_MINUTES, ExcelFormat.INFINITE_HOURS_MINUTES,
                ExcelFormat.INFINITE_HOURS_MINUTES, ExcelFormat.INFINITE_HOURS_MINUTES, ExcelFormat.INFINITE_HOURS_MINUTES,
                ExcelFormat.INFINITE_HOURS_MINUTES, ExcelFormat.INFINITE_HOURS_MINUTES, ExcelFormat.INFINITE_HOURS_MINUTES, ExcelFormat.INFINITE_HOURS_MINUTES,
                ExcelFormat.INFINITE_HOURS_MINUTES, ExcelFormat.INFINITE_HOURS_MINUTES, ExcelFormat.INFINITE_HOURS_MINUTES,
        };
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
                    "", "", lang.get("summary") + ":", toExcelTimeFormat(object.getTimeElapsedSum())
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
                object.getCaseCreated() != null ? object.getCaseCreated() : "",
                toExcelTimeFormat(object.getTimeElapsedNone()),
                toExcelTimeFormat(object.getTimeElapsedWatch()),
                toExcelTimeFormat(object.getTimeElapsedNightWork()),
                toExcelTimeFormat(object.getTimeElapsedTypeSoftInstall()),
                toExcelTimeFormat(object.getTimeElapsedTypeSoftUpdate()),
                toExcelTimeFormat(object.getTimeElapsedTypeSoftConfig()),
                toExcelTimeFormat(object.getTimeElapsedTypeTesting()),
                toExcelTimeFormat(object.getTimeElapsedTypeConsultation()),
                toExcelTimeFormat(object.getTimeElapsedTypeMeeting()),
                toExcelTimeFormat(object.getTimeElapsedTypeDiscussionOfImprovements()),
                toExcelTimeFormat(object.getTimeElapsedTypeLogAnalysis()),
                toExcelTimeFormat(object.getTimeElapsedTypeSolveProblems()),
                toExcelTimeFormat(object.getTimeElapsedSum())
        };
    }
}
