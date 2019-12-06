package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.CaseLink;

/**
 * Ссылки на обращения
 */
public class CaseLinkEvent extends ApplicationEvent implements AbstractCaseEvent {

    private Long caseObjectId;
    private Long personId;
    private ServiceModule serviceModule;
    private CaseLink addedLink;
    private CaseLink removedLink;

    public CaseLinkEvent(Object source, ServiceModule serviceModule, Long personId, Long caseObjectId,
                         CaseLink addedLink, CaseLink removedLink
                            ) {
        super(source);
        this.serviceModule = serviceModule;
        this.personId = personId;
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

    public Long getPersonId() {
        return personId;
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
                ", person=" + personId +
                ", serviceModule=" + serviceModule +
                ", addedLink=" + (addedLink == null ? "" : addedLink.getId()) +
                ", removedLink=" + (removedLink == null ? "" : removedLink.getId()) +
                '}';
    }
}
