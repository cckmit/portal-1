package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Report;

import java.io.InputStream;

public class MailReportEvent extends ApplicationEvent {

    public MailReportEvent(Object source, Report report, InputStream content) {
        super(source);
        this.report = report;
        this.content = content;
    }

    public InputStream getContent() {
        return content;
    }

    public Report getReport() {
        return report;
    }

    private Report report;
    private InputStream content;
}
