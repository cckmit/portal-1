package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;

import java.io.InputStream;
import java.util.Date;

public class DutyLogReportEvent extends ApplicationEvent {
    private final Person initiator;
    private final String title;
    private final Date creationDate;
    private final InputStream content;

    public DutyLogReportEvent(Object source, Person initiator, String title, Date creationDate, InputStream content) {
        super(source);
        this.initiator = initiator;
        this.title = title;
        this.creationDate = creationDate;
        this.content = content;
    }

    public Person getInitiator() {
        return initiator;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public InputStream getContent() {
        return content;
    }
}

