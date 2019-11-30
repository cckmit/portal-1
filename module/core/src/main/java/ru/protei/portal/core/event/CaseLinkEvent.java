package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.Person;

/**
 * Ссылки на обращения
 */
public class CaseLinkEvent extends ApplicationEvent implements AbstractCaseEvent {

    private Long caseObjectId;
    private Person person;
    private ServiceModule serviceModule;
    private CaseLink addedLink;
    private CaseLink removedLink;

    public CaseLinkEvent(Object source, ServiceModule serviceModule, Person person, Long caseObjectId,
                         CaseLink addedLink, CaseLink removedLink
                            ) {
        super(source);
        this.serviceModule = serviceModule;
        this.person = person;
        this.caseObjectId = caseObjectId;
        this.addedLink = addedLink;
        this.removedLink = removedLink;
    }

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public Long getCaseObjectId(){
        return caseObjectId;
    }

    @Override
    public boolean isEagerEvent() {
        return false;
    }

    public Person getPerson() {
        return person;
    }

    public CaseLink getAddedLink() {
        return addedLink;
    }

    public CaseLink getRemovedLink() {
        return removedLink;
    }

    @Override
    public String toString() {
        return "CaseLinkEvent{" +
                "caseObjectId=" + caseObjectId +
                ", person=" + (person == null ? "" : person.getId()) +
                ", serviceModule=" + serviceModule +
                ", addedLink=" + (addedLink == null ? "" : addedLink.getId()) +
                ", removedLink=" + (removedLink == null ? "" : removedLink.getId()) +
                '}';
    }
}
