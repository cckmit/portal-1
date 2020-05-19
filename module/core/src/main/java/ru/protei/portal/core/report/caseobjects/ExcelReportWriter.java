package ru.protei.portal.core.report.caseobjects;

import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
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

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, DateFormat dateFormat, TimeFormatter timeFormatter,
                             boolean isRestricted) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.dateFormat = dateFormat;
        this.timeFormatter = timeFormatter;
        this.isNotRestricted = !isRestricted;
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
        return isNotRestricted ?
                new int[] {
                        3650, 3430, 8570,
                        4590, 4200, 4200,
                        6000, 3350, 4600,
                        4200, 5800, 5800,
                        5800, 5800, 5800,
                        5800, 5800, 5800 } :
                new int[] {
                        3650, 8570,
                        4590, 4200, 4200,
                        6000, 3350, 4600,
                        4200, 5800, 5800,
                        5800, 5800, 5800 };
    }

    @Override
    public String[] getColumnNames() {
        return isNotRestricted ?
                new String[] {
                        "ir_caseno", "ir_private", "ir_name",
                        "ir_company", "ir_initiator", "ir_manager",
                        "ir_product", "ir_importance", "ir_state",
                        "ir_date_created", "ir_date_opened", "ir_date_workaround",
                        "ir_date_customer_test", "ir_date_done", "ir_date_verify",
                        "ir_time_solution_first", "ir_time_solution_full", "ir_time_elapsed" } :
                new String[] {
                        "ir_caseno", "ir_name",
                        "ir_company", "ir_initiator", "ir_manager",
                        "ir_product", "ir_importance", "ir_state",
                        "ir_date_created", "ir_date_opened", "ir_date_workaround",
                        "ir_date_customer_test", "ir_date_done", "ir_date_verify" };
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
                verified = null;

        for (CaseComment comment : comments) {
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
        values.add(issue.getInitiatorCompany() != null && HelperFunc.isNotEmpty(issue.getInitiatorCompany().getCname()) ? transliterate(issue.getInitiatorCompany().getCname(), locale) : "");
        values.add(issue.getInitiator() != null && HelperFunc.isNotEmpty(issue.getInitiator().getDisplayShortName()) ? transliterate(issue.getInitiator().getDisplayShortName(), locale) : "");
        values.add(issue.getManager() != null && HelperFunc.isNotEmpty(issue.getManager().getDisplayShortName()) ? transliterate(issue.getManager().getDisplayShortName(), locale) : "");
        values.add(issue.getProduct() != null && HelperFunc.isNotEmpty(issue.getProduct().getName()) ? issue.getProduct().getName() : "");
        values.add(issue.getImpLevel() != null ? lang.get("importance_" + issue.getImpLevel()) : "");
        values.add(HelperFunc.isNotEmpty(issue.getStateName()) ? issue.getStateName() : "");
        values.add(created != null ? dateFormat.format(created) : "");
        values.add(opened != null ? dateFormat.format(opened) : "");
        values.add(workaround != null ? dateFormat.format(workaround) : "");
        values.add(customerTest != null ? dateFormat.format(customerTest) : "");
        values.add(done != null ? dateFormat.format(done) : "");
        values.add(verified != null ? dateFormat.format(verified) : "");
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
}
