package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

public class PersonCaseFilterEvent extends ApplicationEvent {

    public PersonCaseFilterEvent(Object source, List<CaseObject> issues,
                                           Person personRecipient) {
        super(source);
        this.issues = issues;
        this.recipient = personRecipient;
    }

    public List<CaseObject> getIssues() { return issues; }

    public Person getRecipient() {
        return recipient;
    }

    private final List<CaseObject> issues;
    private final Person recipient;
}
