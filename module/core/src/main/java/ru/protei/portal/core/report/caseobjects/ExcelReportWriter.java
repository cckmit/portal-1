package ru.protei.portal.core.report.caseobjects;

import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.CaseObjectComments;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.JXLSHelper;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static ru.protei.portal.core.model.util.TransliterationUtils.transliterate;

public class ExcelReportWriter implements
        ReportWriter<CaseObjectComments>,
        JXLSHelper.ReportBook.Writer<CaseObjectComments> {

    private final JXLSHelper.ReportBook<CaseObjectComments> book;
    private final Lang.LocalizedLang lang;
    private final DateFormat dateFormat;
    private final TimeFormatter timeFormatter;
    private final boolean isNotRestricted;
    private final String locale;
    private final boolean withDescription;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, DateFormat dateFormat, TimeFormatter timeFormatter,
                             boolean isRestricted, boolean withDescription) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.dateFormat = dateFormat;
        this.timeFormatter = timeFormatter;
        this.isNotRestricted = !isRestricted;
        this.locale = localizedLang.getLanguageTag();
        this.withDescription = withDescription;
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
    public void write(int sheetNumber, List<CaseObjectComments> objects) {
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
        return getColumnsWidth(isNotRestricted, withDescription);
    }

    @Override
    public String[] getColumnNames() {
        return getColumns(isNotRestricted, withDescription);
    }

    @Override
    public Object[] getColumnValues(CaseObjectComments object) {

        CaseObject issue = object.getCaseObject();
        List<CaseComment> comments = object.getCaseComments();

        Date    created = null,
                opened = null,
                workaround = null,
                customerTest = null,
                done = null,
                verified = null,
                critical = null,
                important = null;

        for (CaseComment comment : comments) {

            if (En_ImportanceLevel.IMPORTANT.equals( comment.getCaseImportance() )) important = comment.getCreated();
            if (En_ImportanceLevel.CRITICAL.equals( comment.getCaseImportance() )) critical = comment.getCreated();

            Long stateId = comment.getCaseStateId();
            if (stateId == null) {
                continue;
            }
            if (stateId == CrmConstants.State.CREATED) created = comment.getCreated();
            if (stateId == CrmConstants.State.OPENED) opened = comment.getCreated();
            if (stateId == CrmConstants.State.WORKAROUND) workaround = comment.getCreated();
            if (stateId == CrmConstants.State.TEST_CUST) customerTest = comment.getCreated();
            if (stateId == CrmConstants.State.DONE) done = comment.getCreated();
            if (stateId == CrmConstants.State.VERIFIED) verified = comment.getCreated();
        }

        if (created == null) {
            created = issue.getCreated();
        }

        Long solutionDurationFirst = isNotRestricted ? null : getDurationBetween(created, customerTest, workaround, done);
        Long solutionDurationFull = isNotRestricted ? null : getDurationBetween(created, done, verified);

        List<Object> values = new ArrayList<>();
        values.add("CRM-" + issue.getCaseNumber());
        if (isNotRestricted) values.add(lang.get(issue.isPrivateCase() ? "yes" : "no"));
        values.add(HelperFunc.isNotEmpty(issue.getName()) ? issue.getName() : "");
        if (withDescription) values.add(StringUtils.emptyIfNull(issue.getInfo()));
        values.add(issue.getInitiatorCompany() != null && HelperFunc.isNotEmpty(issue.getInitiatorCompany().getCname()) ? transliterate(issue.getInitiatorCompany().getCname(), locale) : "");
        values.add(issue.getInitiator() != null && HelperFunc.isNotEmpty(issue.getInitiator().getDisplayShortName()) ? transliterate(issue.getInitiator().getDisplayShortName(), locale) : "");
        values.add(issue.getManager() != null && HelperFunc.isNotEmpty(issue.getManager().getDisplayShortName()) ? transliterate(issue.getManager().getDisplayShortName(), locale) : "");
        values.add(issue.getManagerCompanyName() != null ? transliterate(issue.getManagerCompanyName(), locale) : "");
        values.add(issue.getProduct() != null && HelperFunc.isNotEmpty(issue.getProduct().getName()) ? issue.getProduct().getName() : "");
        values.add(issue.getImportanceLevel() != null ? issue.getImportanceLevel().getCode() : "");
        values.add(HelperFunc.isNotEmpty(issue.getStateName()) ? issue.getStateName() : "");
        values.add(created != null ? dateFormat.format(created) : "");
        values.add(opened != null ? dateFormat.format(opened) : "");
        values.add(workaround != null ? dateFormat.format(workaround) : "");
        values.add(customerTest != null ? dateFormat.format(customerTest) : "");
        values.add(done != null ? dateFormat.format(done) : "");
        values.add(verified != null ? dateFormat.format(verified) : "");
        values.add(important != null ? dateFormat.format(important) : "");
        values.add(critical != null ? dateFormat.format(critical) : "");
        if (isNotRestricted) values.add(solutionDurationFirst != null ? timeFormatter.formatHourMinutes(solutionDurationFirst) : "");
        if (isNotRestricted) values.add(solutionDurationFull != null ? timeFormatter.formatHourMinutes(solutionDurationFull) : "");
        if (isNotRestricted) values.add(issue.getTimeElapsed() != null && issue.getTimeElapsed() > 0 ?
                timeFormatter.formatHourMinutes(issue.getTimeElapsed()) : "");

        return values.toArray();
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

    private int[] getColumnsWidth(boolean isNotRestricted, boolean withDescription) {
        List<Integer> columnsWidth = new LinkedList<>();

        columnsWidth.add(3650);

        if (isNotRestricted) {
            columnsWidth.add(3430);
        }

        columnsWidth.add(8570);

        if (withDescription) {
            columnsWidth.add(9000);
        }

        columnsWidth.add(4590);
        columnsWidth.add(4200);
        columnsWidth.add(4200);
        columnsWidth.add(4200);

        columnsWidth.add(6000);
        columnsWidth.add(3350);
        columnsWidth.add(4600);

        columnsWidth.add(4200);
        columnsWidth.add(5800);
        columnsWidth.add(5800);

        columnsWidth.add(5800);
        columnsWidth.add(5800);
        columnsWidth.add(5800);

        columnsWidth.add(5800);
        columnsWidth.add(5800);

        if (isNotRestricted) {
            columnsWidth.add(5800);
            columnsWidth.add(5800);
            columnsWidth.add(5800);
        }

        return toPrimitiveIntegerArray(columnsWidth.toArray(new Integer[]{}));
    }

    private String[] getColumns(boolean isNotRestricted, boolean withDescription) {
        List<String> columns = new LinkedList<>();

        columns.add("ir_caseno");

        if (isNotRestricted) {
            columns.add("ir_private");
        }

        columns.add("ir_name");

        if (withDescription) {
            columns.add("ir_description");
        }

        columns.add("ir_company");
        columns.add("ir_initiator");
        columns.add("ir_manager");
        columns.add("ir_manager_company");

        columns.add("ir_product");
        columns.add("ir_importance");
        columns.add("ir_state");

        columns.add("ir_date_created");
        columns.add("ir_date_opened");
        columns.add("ir_date_workaround");

        columns.add("ir_date_customer_test");
        columns.add("ir_date_done");
        columns.add("ir_date_verify");

        columns.add("ir_date_important");
        columns.add("ir_date_critical");

        if (isNotRestricted) {
            columns.add("ir_time_solution_first");
            columns.add("ir_time_solution_full");
            columns.add("ir_time_elapsed");
        }

        return columns.toArray(new String[]{});
    }

    private int[] toPrimitiveIntegerArray(Integer[] elements) {
        int[] result = new int[elements.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = elements[i];
        }

        return result;
    }
}
