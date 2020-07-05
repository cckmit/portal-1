package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

import java.io.InputStream;

public class MailAbsenceReportEvent extends ApplicationEvent {
    private String name;
    private InputStream content;

    public MailAbsenceReportEvent(Object source, String name, InputStream content) {
        super(source);
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public InputStream getContent() {
        return content;
    }
}
