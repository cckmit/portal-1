package ru.protei.portal.core.report.casetimeelapsed;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.ListBuilder;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.ExcelFormatUtils.ExcelFormat;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.toPrimitiveIntegerArray;
import static ru.protei.portal.core.model.util.TransliterationUtils.transliterate;
import static ru.protei.portal.core.utils.ExcelFormatUtils.toExcelTimeFormat;

public class ExcelReportWriter implements
        ReportWriter<CaseCommentTimeElapsedSum>,
        JXLSHelper.ReportBook.Writer<CaseCommentTimeElapsedSum> {

    private final JXLSHelper.ReportBook<CaseCommentTimeElapsedSum> book;
    private final Lang.LocalizedLang lang;
    private final String locale;
    private final Set<En_TimeElapsedType> timeElapsedTypes;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, Set<En_TimeElapsedType> timeElapsedTypes) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.locale = localizedLang.getLanguageTag();
        this.timeElapsedTypes = timeElapsedTypes;
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
                    .getFormat(getFormats(timeElapsedTypes)[columnIndex]));
        });
    }

    private String[] getFormats(Set<En_TimeElapsedType> timeElapsedTypes) {
        if (isEmpty(timeElapsedTypes)) {
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

        List<String> columnsWidthList = new ListBuilder<String>()
                .add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD)
                .add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD)
                .add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD).add(ExcelFormat.FULL_DATE_TIME)
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.NONE))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.WATCH))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.NIGHT_WORK))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.SOFT_INSTALL))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.SOFT_UPDATE))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.SOFT_CONFIG))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.TESTING))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.CONSULTATION))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.MEETING))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.DISCUSSION_OF_IMPROVEMENTS))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.LOG_ANALYSIS))
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, timeElapsedTypes.contains(En_TimeElapsedType.SOLVE_PROBLEMS))
                .add(ExcelFormat.INFINITE_HOURS_MINUTES).build();

        return columnsWidthList.toArray(new String[]{});
    }

    @Override
    public int[] getColumnsWidth() {
        return getColumnsWidth(timeElapsedTypes);
    }


    private int[] getColumnsWidth(Set<En_TimeElapsedType> timeElapsedTypes) {
        if (isEmpty(timeElapsedTypes)) {
            return new int[] {
                    3650, 3430, 8570,
                    4590, 4200, 4200, 4200,
                    3350, 4600, 4200,
                    5800, 5800, 5800,
                    5800, 5800, 5800,
                    5800, 5800, 5800, 5800,
                    5800, 5800, 5800
            };
        }

        List<Integer> columnsWidthList = new ListBuilder<Integer>()
                .add(3650).add(3430).add(8570)
                .add(4590).add(4200).add(4200).add(4200)
                .add(3350).add(4600).add(4200)
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.NONE))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.WATCH))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.NIGHT_WORK))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.SOFT_INSTALL))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.SOFT_UPDATE))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.SOFT_CONFIG))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.TESTING))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.CONSULTATION))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.MEETING))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.DISCUSSION_OF_IMPROVEMENTS))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.LOG_ANALYSIS))
                .addIf(5800, timeElapsedTypes.contains(En_TimeElapsedType.SOLVE_PROBLEMS))
                .add(5800).build();

        return toPrimitiveIntegerArray(columnsWidthList);
    }

    @Override
    public String[] getColumnNames() {
        return getColumnNames(timeElapsedTypes);
    }

    private String[] getColumnNames(Set<En_TimeElapsedType> timeElapsedTypes) {
        if (isEmpty(timeElapsedTypes)) {
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

        List<String> columnNames = new ListBuilder<String>()
                .add("ir_caseno").add("ir_private").add("ir_name")
                .add("ir_company").add("ir_product").add("ir_performer").add("ir_manager")
                .add("ir_importance").add("ir_state").add("ir_date_created")
                .addIf("ir_work_time_none", timeElapsedTypes.contains(En_TimeElapsedType.NONE))
                .addIf("ir_work_time_watch", timeElapsedTypes.contains(En_TimeElapsedType.WATCH))
                .addIf("ir_work_time_night_work", timeElapsedTypes.contains(En_TimeElapsedType.NIGHT_WORK))
                .addIf("ir_work_time_SoftInstall", timeElapsedTypes.contains(En_TimeElapsedType.SOFT_INSTALL))
                .addIf("ir_work_time_SoftUpdate", timeElapsedTypes.contains(En_TimeElapsedType.SOFT_UPDATE))
                .addIf("ir_work_time_SoftConfig", timeElapsedTypes.contains(En_TimeElapsedType.SOFT_CONFIG))
                .addIf("ir_work_time_Testing", timeElapsedTypes.contains(En_TimeElapsedType.TESTING))
                .addIf("ir_work_time_Consultation", timeElapsedTypes.contains(En_TimeElapsedType.CONSULTATION))
                .addIf("ir_work_time_Meeting", timeElapsedTypes.contains(En_TimeElapsedType.MEETING))
                .addIf("ir_work_time_DiscussionOfImprovements", timeElapsedTypes.contains(En_TimeElapsedType.DISCUSSION_OF_IMPROVEMENTS))
                .addIf("ir_work_time_LogAnalysis", timeElapsedTypes.contains(En_TimeElapsedType.LOG_ANALYSIS))
                .addIf("ir_work_time_SolveProblems", timeElapsedTypes.contains(En_TimeElapsedType.SOLVE_PROBLEMS))
                .add("ir_work_time_all").build();

        return columnNames.toArray(new String[]{});
    }

    @Override
    public Object[] getColumnValues(CaseCommentTimeElapsedSum object) {
        if (object.getAuthorDisplayName() == null) {
            Object[] values = new Object[getColumnNames(timeElapsedTypes).length];

            for (int i = 0; i < values.length - 2; i++) {
                values[i] = "";
            }

            values[values.length - 2] = lang.get("summary") + ":";
            values[values.length - 1] = toExcelTimeFormat(object.getTimeElapsedSum());

            return values;
        }

        if (isEmpty(timeElapsedTypes)) {
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

        List<Object> columnValues = new ListBuilder<>()
                .add("CRM-" + object.getCaseNumber())
                .add(lang.get(object.isCasePrivateCase() ? "yes" : "no"))
                .add(HelperFunc.isNotEmpty(object.getCaseName()) ? object.getCaseName() : "")
                .add(HelperFunc.isNotEmpty(object.getCaseCompanyName()) ? transliterate(object.getCaseCompanyName(), locale) : "")
                .add(HelperFunc.isNotEmpty(object.getProductName()) ? object.getProductName() : "")
                .add(HelperFunc.isNotEmpty(object.getAuthorDisplayName()) ? transliterate(object.getAuthorDisplayName(), locale) : "")
                .add(HelperFunc.isNotEmpty(object.getCaseManagerDisplayName()) ? transliterate(object.getCaseManagerDisplayName(), locale) : "")
                .add(object.getImportanceLevel() != null ? object.getImportanceLevel().getCode() : "")
                .add(HelperFunc.isNotEmpty(object.getCaseStateName()) ? object.getCaseStateName() : "")
                .add(object.getCaseCreated() != null ? object.getCaseCreated() : "")
                .addIf(toExcelTimeFormat(object.getTimeElapsedNone()), timeElapsedTypes.contains(En_TimeElapsedType.NONE))
                .addIf(toExcelTimeFormat(object.getTimeElapsedWatch()), timeElapsedTypes.contains(En_TimeElapsedType.WATCH))
                .addIf(toExcelTimeFormat(object.getTimeElapsedNightWork()), timeElapsedTypes.contains(En_TimeElapsedType.NIGHT_WORK))
                .addIf(toExcelTimeFormat(object.getTimeElapsedTypeSoftInstall()), timeElapsedTypes.contains(En_TimeElapsedType.SOFT_INSTALL))
                .addIf(toExcelTimeFormat(object.getTimeElapsedTypeSoftUpdate()), timeElapsedTypes.contains(En_TimeElapsedType.SOFT_UPDATE))
                .addIf(toExcelTimeFormat(object.getTimeElapsedTypeSoftConfig()), timeElapsedTypes.contains(En_TimeElapsedType.SOFT_CONFIG))
                .addIf(toExcelTimeFormat(object.getTimeElapsedTypeTesting()), timeElapsedTypes.contains(En_TimeElapsedType.TESTING))
                .addIf(toExcelTimeFormat(object.getTimeElapsedTypeConsultation()), timeElapsedTypes.contains(En_TimeElapsedType.CONSULTATION))
                .addIf(toExcelTimeFormat(object.getTimeElapsedTypeMeeting()), timeElapsedTypes.contains(En_TimeElapsedType.MEETING))
                .addIf(toExcelTimeFormat(object.getTimeElapsedTypeDiscussionOfImprovements()), timeElapsedTypes.contains(En_TimeElapsedType.DISCUSSION_OF_IMPROVEMENTS))
                .addIf(toExcelTimeFormat(object.getTimeElapsedTypeLogAnalysis()), timeElapsedTypes.contains(En_TimeElapsedType.LOG_ANALYSIS))
                .addIf(toExcelTimeFormat(object.getTimeElapsedTypeSolveProblems()), timeElapsedTypes.contains(En_TimeElapsedType.SOLVE_PROBLEMS))
                .add(toExcelTimeFormat(object.getTimeElapsedSum())).build();

        return columnValues.toArray();
    }
}
