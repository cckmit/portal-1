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
        List<Integer> columnsWidthList = new ColumnsListBuilder<Integer>()
                .add(3650).addIf(3430, isNotRestricted).add(8570).addIf(9000, withDescription)
                .add(4590).add(4200).add(4200).add(4200)
                .add(6000).add(3350).add(4600)
                .add(4200).add(5800).add(5800)
                .add(5800).add(5800).add(5800)
                .add(5800).add(5800)
                .addIf(5800, isNotRestricted).addIf(5800, isNotRestricted).addIf(5800, isNotRestricted)
                .build();

        return toPrimitiveIntegerArray(columnsWidthList.toArray(new Integer[]{}));
    }

    private String[] getColumns(boolean isNotRestricted, boolean withDescription) {
        List<String> columnsList = new ColumnsListBuilder<String>()
                .add("ir_caseno").addIf("ir_private", isNotRestricted).add("ir_name").addIf("ir_description", withDescription)
                .add("ir_company").add("ir_initiator").add("ir_manager").add("ir_manager_company")
                .add("ir_product").add("ir_importance").add("ir_state")
                .add("ir_date_created").add("ir_date_opened").add("ir_date_workaround")
                .add("ir_date_customer_test").add("ir_date_done").add("ir_date_verify")
                .add("ir_date_important").add("ir_date_critical")
                .addIf("ir_time_solution_first", isNotRestricted).addIf("ir_time_solution_full", isNotRestricted).addIf("ir_time_elapsed", isNotRestricted)
                .build();

        return columnsList.toArray(new String[]{});
    }

    private int[] toPrimitiveIntegerArray(Integer[] elements) {
        int[] result = new int[elements.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = elements[i];
        }

        return result;
    }

    private static class ColumnsListBuilder<T> {
        private List<T> list = new LinkedList<>();

        ColumnsListBuilder<T> add(T element) {
            list.add(element);
            return this;
        }

        ColumnsListBuilder<T> addIf(T element, boolean condition) {
            if (condition) {
                list.add(element);
            }

            return this;
        }

        List<T> build() {
            return list;
        }
    }
}
