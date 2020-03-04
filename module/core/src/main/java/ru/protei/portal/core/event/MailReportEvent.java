package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Report;

public class MailReportEvent extends ApplicationEvent {

    public MailReportEvent(Object source, Report report) {
        super(source);
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

    private Report report;
}
