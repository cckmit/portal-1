package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.IssueCreateRequest;

import java.util.Objects;

/**
 * Created by michael on 04.05.17.
 */
public class CaseObjectCreateEvent extends ApplicationEvent implements AbstractCaseEvent {

    private Long personId;
    private ServiceModule serviceModule;
    private CaseObject caseObject;

    public CaseObjectCreateEvent(Object source, ServiceModule serviceModule, Long personId, CaseObject caseObject) {
        super(source);
        this.serviceModule = serviceModule;
        this.personId = personId;
        this.caseObject = caseObject;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public boolean isCreateEvent() {
        return true;
    }

    @Override
    public Long getCaseObjectId() {
        CaseObject caseObject = getCaseObject();
        if (caseObject == null) return null;
        return caseObject.getId();
    }

    @Override
    public boolean isEagerEvent() {
        CaseObject caseObject = getCaseObject();
        if (caseObject == null) return false;
        return Objects.equals(En_ExtAppType.REDMINE.getCode(), caseObject.getExtAppType());
    }

    public ServiceModule getServiceModule() {
        return serviceModule != null ? serviceModule : ServiceModule.GENERAL;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    @Override
    public String toString() {
        return "CaseObjectEvent{" +
                "caseObjectId=" + getCaseObjectId() +
                ", isEagerEvent=" + isEagerEvent() +
                ", caseObject=" + asString(getCaseObject()) +
                ", personId=" + personId +
                '}';
    }

    private String asString(CaseObject caseObject) {
        if (caseObject == null) return null;
        return "CaseObject{" +
                "id=" + caseObject.getId() +
                ", caseNumber=" + caseObject.getCaseNumber() +
                ", typeId=" + caseObject.getTypeId() +
                ", extId='" + caseObject.getExtId() + '\'' +
                '}';
    }
}
