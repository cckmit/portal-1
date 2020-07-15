package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;

import java.io.InputStream;

public class AbsenceReportEvent extends ApplicationEvent {
    private final Person initiator;
    private final String title;
    private final InputStream content;

    public AbsenceReportEvent(Object source, Person initiator, String title, InputStream content) {
        super(source);
        this.initiator = initiator;
        this.title = title;
        this.content = content;
    }

    public Person getInitiator() {
        return initiator;
    }

    public String getTitle() {
        return title;
    }

    public InputStream getContent() {
        return content;
    }
}
