package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

public class DocumentDocFileUpdatedByMemberEvent extends ApplicationEvent {

    public DocumentDocFileUpdatedByMemberEvent(Object source, Person initiator, Document document, List<Person> personList, String comment) {
        super(source);
        this.initiator = initiator;
        this.document = document;
        this.personList = personList;
        this.comment = comment;
    }

    public Person getInitiator() {
        return initiator;
    }

    public Document getDocument() {
        return document;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public String getComment() {
        return comment;
    }

    private Person initiator;
    private Document document;
    private List<Person> personList;
    private String comment;
}
