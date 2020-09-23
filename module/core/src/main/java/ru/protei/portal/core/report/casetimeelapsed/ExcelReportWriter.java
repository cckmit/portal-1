package ru.protei.portal.core.report.casetimeelapsed;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.ColumnsListBuilder;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.ExcelFormatUtils.ExcelFormat;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
    private long timeElapsedSum;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, Set<En_TimeElapsedType> timeElapsedTypes) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.locale = localizedLang.getLanguageTag();
        this.timeElapsedTypes = timeElapsedTypes;
        this.timeElapsedSum = 0;
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

        List<Integer> columnsWidthList = new ColumnsListBuilder<Integer>()
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

        List<String> columnNames = new ColumnsListBuilder<String>()
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
            values[values.length - 1] = toExcelTimeFormat(isEmpty(timeElapsedTypes) ? object.getTimeElapsedSum() : timeElapsedSum);

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

        List<Object> values = new ArrayList<>();
        long timeElapsedRowSum = 0;

        values.add("CRM-" + object.getCaseNumber());
        values.add(lang.get(object.isCasePrivateCase() ? "yes" : "no"));
        values.add(HelperFunc.isNotEmpty(object.getCaseName()) ? object.getCaseName() : "");
        values.add(HelperFunc.isNotEmpty(object.getCaseCompanyName()) ? transliterate(object.getCaseCompanyName(), locale) : "");
        values.add(HelperFunc.isNotEmpty(object.getProductName()) ? object.getProductName() : "");
        values.add(HelperFunc.isNotEmpty(object.getAuthorDisplayName()) ? transliterate(object.getAuthorDisplayName(), locale) : "");
        values.add(HelperFunc.isNotEmpty(object.getCaseManagerDisplayName()) ? transliterate(object.getCaseManagerDisplayName(), locale) : "");
        values.add(object.getImportanceLevel() != null ? object.getImportanceLevel().getCode() : "");
        values.add(HelperFunc.isNotEmpty(object.getCaseStateName()) ? object.getCaseStateName() : "");
        values.add(object.getCaseCreated() != null ? object.getCaseCreated() : "");

        if (timeElapsedTypes.contains(En_TimeElapsedType.NONE)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedNone());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.WATCH)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedWatch());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.NIGHT_WORK)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedNightWork());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.SOFT_INSTALL)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedTypeSoftInstall());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.SOFT_UPDATE)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedTypeSoftUpdate());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.SOFT_CONFIG)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedTypeSoftConfig());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.TESTING)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedTypeTesting());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.CONSULTATION)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedTypeConsultation());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.MEETING)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedTypeMeeting());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.DISCUSSION_OF_IMPROVEMENTS)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedTypeDiscussionOfImprovements());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.LOG_ANALYSIS)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedTypeLogAnalysis());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }
        if (timeElapsedTypes.contains(En_TimeElapsedType.SOLVE_PROBLEMS)) {
            long timeElapsed = getTimeElapsed(object.getTimeElapsedTypeSolveProblems());
            values.add(toExcelTimeFormat(timeElapsed));
            timeElapsedSum += timeElapsed;
            timeElapsedRowSum += timeElapsed;
        }

        values.add(toExcelTimeFormat(timeElapsedRowSum));

        return values.toArray();
    }

    private long getTimeElapsed(Long timeElapsed) {
        return timeElapsed == null ? 0 : timeElapsed;
    }
}
