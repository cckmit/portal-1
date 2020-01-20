package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

public class DocumentMemberAddedEvent extends ApplicationEvent {

    public DocumentMemberAddedEvent(Object source, Document document, List<Person> personList) {
        super(source);
        this.document = document;
        this.personList = personList;
    }

    public Document getDocument() {
        return document;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    private Document document;
    private List<Person> personList;
}
