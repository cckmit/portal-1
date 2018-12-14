package ru.protei.portal.core.service.report.caseobjects;

import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.CaseObjectComments;
import ru.protei.portal.core.service.report.ReportWriter;
import ru.protei.portal.core.utils.JXLSHelper;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ExcelReportWriter implements
        ReportWriter<CaseObjectComments>,
        JXLSHelper.ReportBook.Writer<CaseObjectComments> {

    private final JXLSHelper.ReportBook<CaseObjectComments> book;
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
        return new int[] {
                3650, 3430, 8570,
                4590, 4200, 4200,
                6000, 3350, 4600,
                4200, 5800, 5800,
                5800, 5800, 5800,
                5800, 5800, 5800
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {
                "ir_caseno", "ir_private", "ir_name",
                "ir_company", "ir_initiator", "ir_manager",
                "ir_product", "ir_importance", "ir_state",
                "ir_date_created", "ir_date_opened", "ir_date_workaround",
                "ir_date_customer_test", "ir_date_done", "ir_date_verify",
                "ir_time_solution_first", "ir_time_solution_full", "ir_time_elapsed"
        };
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
            En_CaseState state = En_CaseState.getById(comment.getCaseStateId());
            if (state == null) {
                continue;
            }
            switch (state) {
                case CREATED: created = comment.getCreated(); break;
                case OPENED: opened = comment.getCreated(); break;
                case WORKAROUND: workaround = comment.getCreated(); break;
                case TEST_CUST: customerTest = comment.getCreated(); break;
                case DONE: done = comment.getCreated(); break;
                case VERIFIED: verified = comment.getCreated(); break;
            }
        }

        if (created == null) {
            created = issue.getCreated();
        }

        Long solutionDurationFirst = getDurationBetween(created, customerTest, workaround, done);
        Long solutionDurationFull = getDurationBetween(created, done, verified);

        return new Object[] {
                "CRM-" + issue.getCaseNumber(),
                lang.get(issue.isPrivateCase() ? "yes" : "no"),
                HelperFunc.isNotEmpty(issue.getName()) ? issue.getName() : "",
                issue.getInitiatorCompany() != null && HelperFunc.isNotEmpty(issue.getInitiatorCompany().getCname()) ? issue.getInitiatorCompany().getCname() : "",
                issue.getInitiator() != null && HelperFunc.isNotEmpty(issue.getInitiator().getDisplayShortName()) ? issue.getInitiator().getDisplayShortName() : "",
                issue.getManager() != null && HelperFunc.isNotEmpty(issue.getManager().getDisplayShortName()) ? issue.getManager().getDisplayShortName() : "",
                issue.getProduct() != null && HelperFunc.isNotEmpty(issue.getProduct().getName()) ? issue.getProduct().getName() : "",
                issue.getImpLevel() != null ? lang.get("importance_" + String.valueOf(issue.getImpLevel())) : "",
                issue.getState() != null ? lang.get("case_state_" + String.valueOf(issue.getState().getId())) : "",
                created != null ? dateFormat.format(created) : "",
                opened != null ? dateFormat.format(opened) : "",
                workaround != null ? dateFormat.format(workaround) : "",
                customerTest != null ? dateFormat.format(customerTest) : "",
                done != null ? dateFormat.format(done) : "",
                verified != null ? dateFormat.format(verified) : "",
                solutionDurationFirst != null ? timeFormatter.formatHourMinutes(solutionDurationFirst) : "",
                solutionDurationFull != null ? timeFormatter.formatHourMinutes(solutionDurationFull) : "",
                issue.getTimeElapsed() != null && issue.getTimeElapsed() > 0 ?
                        timeFormatter.formatHourMinutes(issue.getTimeElapsed())
                        : ""
        };
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
