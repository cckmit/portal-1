package ru.protei.portal.core.report.caseobjects;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.struct.ListBuilder;
import ru.protei.portal.core.model.struct.caseobjectreport.CaseObjectReportRequest;
import ru.protei.portal.core.model.struct.caseobjectreport.CaseObjectReportRow;
import ru.protei.portal.core.model.struct.caseobjectreport.CaseObjectReportTimeElapsedGroupRow;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.ExcelFormatUtils.ExcelFormat;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.util.TransliterationUtils.transliterate;
import static ru.protei.portal.core.utils.ExcelFormatUtils.toDaysHoursMinutes;
import static ru.protei.portal.core.utils.ExcelFormatUtils.toExcelTimeFormat;

public class ExcelReportWriter implements
        ReportWriter<CaseObjectReportRow>,
        JXLSHelper.ReportBook.Writer<CaseObjectReportRow> {

    private final JXLSHelper.ReportBook<CaseObjectReportRow> book;
    private final Lang.LocalizedLang lang;
    private final EnumLangUtil enumLangUtil;
    private final boolean isNotRestricted;
    private final String locale;
    private final boolean withDescription;
    private final boolean withTags;
    private final boolean withLinkedIssues;
    private final boolean isHumanReadable;
    private final boolean withImportanceHistory;
    private final boolean withDeadlineAndWorkTrigger;
    private final boolean withTimeElapsedGroupType;
    private final boolean withTimeElapsedGroupDepartment;
    private final boolean withTimeElapsedGroupAuthor;
    private final String[] formats;
    static private final int COLUMN_COUNT = 17;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang,
                             EnumLangUtil enumLangUtil,
                             boolean isRestricted,
                             boolean withDescription,
                             boolean withTags,
                             boolean withLinkedIssues,
                             boolean isHumanReadable,
                             boolean withImportanceHistory,
                             boolean withDeadlineAndWorkTrigger,
                             boolean withTimeElapsedGroupType,
                             boolean withTimeElapsedGroupDepartment,
                             boolean withTimeElapsedGroupAuthor
                             ) {

        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.enumLangUtil = enumLangUtil;
        this.isNotRestricted = !isRestricted;
        this.locale = localizedLang.getLanguageTag();
        this.withDescription = withDescription;
        this.withTags = withTags;
        this.withLinkedIssues = withLinkedIssues;
        this.isHumanReadable = isHumanReadable;
        this.withImportanceHistory = withImportanceHistory;
        this.withDeadlineAndWorkTrigger = withDeadlineAndWorkTrigger;
        this.withTimeElapsedGroupType = withTimeElapsedGroupType;
        this.withTimeElapsedGroupDepartment = withTimeElapsedGroupDepartment;
        this.withTimeElapsedGroupAuthor = withTimeElapsedGroupAuthor;
        this.formats = getFormats(
                isNotRestricted,
                withDescription,
                withTags,
                withLinkedIssues,
                isHumanReadable,
                withImportanceHistory,
                withDeadlineAndWorkTrigger,
                withTimeElapsedGroupType,
                withTimeElapsedGroupDepartment,
                withTimeElapsedGroupAuthor
        );
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
    public void write(int sheetNumber, List<CaseObjectReportRow> objects) {
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
        return getColumnsWidth(
                isNotRestricted,
                withDescription,
                withTags,
                withLinkedIssues,
                isHumanReadable,
                withImportanceHistory,
                withDeadlineAndWorkTrigger,
                withTimeElapsedGroupType,
                withTimeElapsedGroupDepartment,
                withTimeElapsedGroupAuthor
        );
    }

    @Override
    public String[] getLangColumnNames() {
        return getColumns(
                isNotRestricted,
                withDescription,
                withTags,
                withLinkedIssues,
                isHumanReadable,
                withImportanceHistory,
                withDeadlineAndWorkTrigger,
                withTimeElapsedGroupType,
                withTimeElapsedGroupDepartment,
                withTimeElapsedGroupAuthor
        );
    }

    @Override
    public Object[] getColumnValues(CaseObjectReportRow row) {
        List<Object> values = new ArrayList<>();

        if (row instanceof CaseObjectReportRequest) {
            // Количество колонок важно
            // для других типов строк COLUMN_COUNT = 17
            CaseObjectReportRequest object = (CaseObjectReportRequest)row;

            CaseObject issue = object.getCaseObject();
            List<History> histories = object.getHistories();

            Date    created = null,
                    opened = null,
                    workaround = null,
                    customerTest = null,
                    done = null,
                    verified = null,
                    critical = null,
                    important = null;

            for (History history : histories) {
                if (history.getType() == En_HistoryType.CASE_IMPORTANCE) {
                    if (Objects.equals(CrmConstants.ImportanceLevel.IMPORTANT, history.getNewId().intValue())) important = history.getDate();
                    if (Objects.equals(CrmConstants.ImportanceLevel.CRITICAL, history.getNewId().intValue())) critical = history.getDate();
                }

                if (history.getType() == En_HistoryType.CASE_STATE) {
                    Long stateId = history.getNewId();

                    if (Objects.equals(stateId, CrmConstants.State.CREATED)) created = history.getDate();
                    if (Objects.equals(stateId, CrmConstants.State.OPENED)) opened = history.getDate();
                    if (Objects.equals(stateId, CrmConstants.State.WORKAROUND)) workaround = history.getDate();
                    if (Objects.equals(stateId, CrmConstants.State.TEST_CUST)) customerTest = history.getDate();
                    if (Objects.equals(stateId, CrmConstants.State.DONE)) done = history.getDate();
                    if (Objects.equals(stateId, CrmConstants.State.VERIFIED)) verified = history.getDate();
                }
            }

            if (created == null) {
                created = issue.getCreated();
            }

            long timeElapsedInSelectedDuration = 0;

            if (isNotRestricted) {
                timeElapsedInSelectedDuration = object.getCaseComments()
                        .stream()
                        .filter(comment -> comment.getTimeElapsed() != null)
                        .filter(comment -> isDateInAnyRange(comment.getCreated(), makeInterval(object.getCreatedRange()), makeInterval(object.getModifiedRange())))
                        .mapToLong(CaseComment::getTimeElapsed)
                        .sum();
            }

            Long solutionDurationFirst = isNotRestricted ? getDurationBetween(created, customerTest, workaround, done) : null;
            Long solutionDurationFull = isNotRestricted ? getDurationBetween(created, done, verified) : null;

            values.add("CRM-" + issue.getCaseNumber());
            if (isNotRestricted) values.add(lang.get(issue.isPrivateCase() ? "yes" : "no"));
            values.add(HelperFunc.isNotEmpty(issue.getName()) ? issue.getName() : "");
            if (withDescription) values.add(StringUtils.emptyIfNull(issue.getInfo()));
            values.add(issue.getInitiatorCompany() != null && HelperFunc.isNotEmpty(issue.getInitiatorCompany().getCname()) ? transliterate(issue.getInitiatorCompany().getCname(), locale) : "");
            values.add(issue.getInitiator() != null && HelperFunc.isNotEmpty(issue.getInitiator().getDisplayShortName()) ? transliterate(issue.getInitiator().getDisplayShortName(), locale) : "");
            values.add(issue.getManager() != null && HelperFunc.isNotEmpty(issue.getManager().getDisplayShortName()) ? transliterate(issue.getManager().getDisplayShortName(), locale) : "");
            values.add(issue.getManagerCompanyName() != null ? transliterate(issue.getManagerCompanyName(), locale) : "");
            values.add(issue.getProduct() != null && HelperFunc.isNotEmpty(issue.getProduct().getName()) ? issue.getProduct().getName() : "");
            values.add(issue.getImportanceCode() != null ? issue.getImportanceCode() : "");
            values.add(HelperFunc.isNotEmpty(issue.getStateName()) ? issue.getStateName() : "");
            if (withTags) values.add(String.join(",", toList(emptyIfNull(object.getCaseTags()), CaseTag::getName)));
            if (withLinkedIssues) values.add(getCaseNumbersAsString(object.getCaseLinks(), lang));
            if (isNotRestricted && withDeadlineAndWorkTrigger) values.add(issue.getDeadline() != null ? new Date(issue.getDeadline()) : "");
            if (isNotRestricted && withDeadlineAndWorkTrigger) values.add(issue.getWorkTrigger() != null ? enumLangUtil.workTriggerLang(issue.getWorkTrigger(), lang.getLanguageTag()) : "");
            values.add(created != null ? created : "");
            values.add(opened != null ? opened : "");
            values.add(workaround != null ? workaround : "");
            values.add(customerTest != null ? customerTest : "");
            values.add(done != null ? done : "");
            values.add(verified != null ? verified : "");

            if (withImportanceHistory) values.add(important != null ? important : "");
            if (withImportanceHistory) values.add(critical != null ? critical : "");

            if (isNotRestricted) values.add(solutionDurationFirst == null ? "" : toExcelTimeFormat(solutionDurationFirst));
            if (isNotRestricted && isHumanReadable) values.add(solutionDurationFirst == null ? "" : toDaysHoursMinutes(solutionDurationFirst));

            if (isNotRestricted) values.add(solutionDurationFull == null ? "" : toExcelTimeFormat(solutionDurationFull));
            if (isNotRestricted && isHumanReadable) values.add(solutionDurationFull == null ? "" : toDaysHoursMinutes(solutionDurationFull));

            if (isNotRestricted) values.add(issue.getTimeElapsed() != null && issue.getTimeElapsed() > 0 ? toExcelTimeFormat(issue.getTimeElapsed()) : "");
            if (isNotRestricted) values.add(toExcelTimeFormat(timeElapsedInSelectedDuration));
        } else if (row instanceof CaseObjectReportTimeElapsedGroupRow) {
            CaseObjectReportTimeElapsedGroupRow timeElapsedGroupRow = (CaseObjectReportTimeElapsedGroupRow)row;
            int columnCount = COLUMN_COUNT;
            if (withTags) columnCount++;
            if (withLinkedIssues) columnCount++;
            if (isNotRestricted && withDeadlineAndWorkTrigger) columnCount++;
            if (isNotRestricted && withDeadlineAndWorkTrigger) columnCount++;
            if (withImportanceHistory) columnCount++;
            if (withImportanceHistory) columnCount++;
            if (isNotRestricted) columnCount++;
            if (isNotRestricted && isHumanReadable) columnCount++;
            if (isNotRestricted) columnCount++;;
            if (isNotRestricted && isHumanReadable) columnCount++;;
            for (int i = 0; i < columnCount; i++) {
                values.add("");
            }
            values.add(toExcelTimeFormat(timeElapsedGroupRow.getTimeElapsed()));
            if (withTimeElapsedGroupType) values.add(enumLangUtil.timeElapsedTypeLang(timeElapsedGroupRow.getTimeElapsedType(), lang.getLanguageTag()));
            if (withTimeElapsedGroupDepartment) values.add(TransliterationUtils.transliterate(timeElapsedGroupRow.getEmployeeDepartment(), lang.getLanguageTag()));
            if (withTimeElapsedGroupAuthor) values.add(TransliterationUtils.transliterate(timeElapsedGroupRow.getEmployeeName(), lang.getLanguageTag()));
        }

        return values.toArray();
    }

    @Override
    public CellStyle getCellStyle(Workbook workbook, int columnIndex) {
        return book.makeCellStyle(columnIndex, cs -> {
            cs.setFont(book.getDefaultFont());
            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            cs.setDataFormat(workbook.createDataFormat().getFormat(formats[columnIndex]));
        });
    }

    private boolean isDateInAnyRange(final Date date, Interval... intervals) {
        return Arrays.stream(intervals).anyMatch(interval -> isDateInRange(date, interval));
    }

    private boolean isDateInRange(Date date, Interval dateRange) {
        if (date == null || dateRange == null) {
            return false;
        }

        if (dateRange.getFrom() == null) {
            return dateRange.getTo() == null || date.before(dateRange.getTo());
        }

        if (dateRange.getTo() == null) {
            return dateRange.getFrom() == null || date.after(dateRange.getFrom());
        }

        if (date.before(dateRange.getFrom())) {
            return false;
        }

        if (date.after(dateRange.getTo())) {
            return false;
        }

        return true;
    }

    private Long getDurationBetween(Date from, Date... toList) {
        if (toList == null || from == null) {
            return null;
        }
        Date to = null;
        for (Date t : toList) {
            if (t != null) {
                to = t;
                break;
            }
        }
        if (to != null) {
            for (Date t : toList) {
                if (t != null && t.after(to)) {
                    to = t;
                }
            }
            Long minutes = to.getTime() / 60000L - from.getTime() / 60000L;
            return minutes > 0 ? minutes : null;
        }
        return null;
    }

    private String getCaseNumbersAsString(Collection<CaseLink> caseLinks, final Lang.LocalizedLang lang) {
        return stream(caseLinks)
                .map(caseLink -> ofNullable(caseLink.getCaseInfo())
                        .map(info -> lang.get("crmPrefix") + caseLink.getCaseInfo().getCaseNumber())
                        .orElse(caseLink.getRemoteId()))
                .collect(Collectors.joining(","));
    }

    private String[] getFormats(boolean isNotRestricted, boolean withDescription, boolean withTags, boolean withLinkedIssues,
                                boolean isHumanReadable, boolean withImportanceHistory, boolean withDeadlineAndWorkTrigger,
                                boolean withTimeElapsedGroupType, boolean withTimeElapsedGroupDepartment, boolean withTimeElapsedGroupAuthor) {
        List<String> formatList = new ListBuilder<String>()
                .add(ExcelFormat.STANDARD).addIf(ExcelFormat.STANDARD, isNotRestricted).add(ExcelFormat.STANDARD).addIf(ExcelFormat.STANDARD, withDescription)
                .add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD)
                .add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD).addIf(ExcelFormat.STANDARD, withTags).addIf(ExcelFormat.STANDARD, withLinkedIssues)
                .addIf(ExcelFormat.FULL_DATE, isNotRestricted && withDeadlineAndWorkTrigger)
                .addIf(ExcelFormat.STANDARD, isNotRestricted && withDeadlineAndWorkTrigger)
                .add(ExcelFormat.DATE_TIME).add(ExcelFormat.DATE_TIME).add(ExcelFormat.DATE_TIME)
                .add(ExcelFormat.DATE_TIME).add(ExcelFormat.DATE_TIME).add(ExcelFormat.DATE_TIME)
                .addIf(ExcelFormat.DATE_TIME, withImportanceHistory).addIf(ExcelFormat.DATE_TIME, withImportanceHistory)
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, isNotRestricted).addIf(ExcelFormat.STANDARD, isNotRestricted && isHumanReadable)
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, isNotRestricted).addIf(ExcelFormat.STANDARD, isNotRestricted && isHumanReadable)
                .addIf(ExcelFormat.INFINITE_HOURS_MINUTES, isNotRestricted).addIf(ExcelFormat.INFINITE_HOURS_MINUTES, isNotRestricted)
                .addIf(ExcelFormat.STANDARD, withTimeElapsedGroupType)
                .addIf(ExcelFormat.STANDARD, withTimeElapsedGroupDepartment)
                .addIf(ExcelFormat.STANDARD, withTimeElapsedGroupAuthor)
                .build();

        return formatList.toArray(new String[]{});
    }

    private int[] getColumnsWidth(boolean isNotRestricted, boolean withDescription, boolean withTags, boolean withLinkedIssues,
                                  boolean isHumanReadable, boolean withImportanceHistory, boolean withDeadlineAndWorkTrigger,
                                  boolean withTimeElapsedGroupType, boolean withTimeElapsedGroupDepartment, boolean withTimeElapsedGroupAuthor) {
        List<Integer> columnsWidthList = new ListBuilder<Integer>()
                .add(3650).addIf(3430, isNotRestricted).add(8570).addIf(9000, withDescription)
                .add(4590).add(4200).add(4200).add(4200)
                .add(6000).add(3350).add(4600).addIf(4600, withTags).addIf(6000, withLinkedIssues)
                .addIf(5800, isNotRestricted && withDeadlineAndWorkTrigger)
                .addIf(5800, isNotRestricted && withDeadlineAndWorkTrigger)
                .add(4200).add(5800).add(5800)
                .add(5800).add(5800).add(5800)
                .addIf(5800, withImportanceHistory).addIf(5800, withImportanceHistory)
                .addIf(12000, isNotRestricted).addIf(12000, isNotRestricted && isHumanReadable)
                .addIf(12000, isNotRestricted).addIf(12000, isNotRestricted && isHumanReadable)
                .addIf(5800, isNotRestricted).addIf(12000, isNotRestricted)
                .addIf(5800, withTimeElapsedGroupType)
                .addIf(12000, withTimeElapsedGroupDepartment)
                .addIf(12000, withTimeElapsedGroupAuthor)
                .build();

        return toPrimitiveIntegerArray(columnsWidthList);
    }

    private String[] getColumns(boolean isNotRestricted, boolean withDescription, boolean withTags, boolean withLinkedIssues,
                                boolean isHumanReadable, boolean withImportanceHistory, boolean withDeadlineAndWorkTrigger,
                                boolean withTimeElapsedGroupType, boolean withTimeElapsedGroupDepartment, boolean withTimeElapsedGroupAuthor) {
        List<String> columnsList = new ListBuilder<String>()
                .add("ir_caseno").addIf("ir_private", isNotRestricted).add("ir_name").addIf("ir_description", withDescription)
                .add("ir_company").add("ir_initiator").add("ir_manager").add("ir_manager_company")
                .add("ir_product").add("ir_importance").add("ir_state").addIf("ir_tags", withTags).addIf("ir_links", withLinkedIssues)
                .addIf("ir_deadline", isNotRestricted && withDeadlineAndWorkTrigger)
                .addIf("ir_work_trigger", isNotRestricted && withDeadlineAndWorkTrigger)
                .add("ir_date_created").add("ir_date_opened").add("ir_date_workaround")
                .add("ir_date_customer_test").add("ir_date_done").add("ir_date_verify")
                .addIf("ir_date_important", withImportanceHistory).addIf("ir_date_critical", withImportanceHistory)
                .addIf("ir_time_solution_first", isNotRestricted).addIf("ir_time_solution_first_with_days", isNotRestricted && isHumanReadable)
                .addIf("ir_time_solution_full", isNotRestricted).addIf("ir_time_solution_full_with_days", isNotRestricted && isHumanReadable)
                .addIf("ir_time_elapsed", isNotRestricted).addIf("ir_time_elapsed_selected_range", isNotRestricted)
                .addIf("ir_work_time_type", withTimeElapsedGroupType)
                .addIf("ir_work_time_department", withTimeElapsedGroupDepartment)
                .addIf("ir_work_time_employee", withTimeElapsedGroupAuthor)
                .build();

        return columnsList.toArray(new String[]{});
    }
}
