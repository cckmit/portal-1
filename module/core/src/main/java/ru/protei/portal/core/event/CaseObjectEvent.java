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
public class CaseObjectEvent extends ApplicationEvent implements AbstractCaseEvent {

    private CaseObject newState;
    private CaseObject oldState;
    private Long personId;
    private ServiceModule serviceModule;
    private IssueCreateRequest issueCreateRequest;
    private boolean isCreateEvent;

    public CaseObjectEvent(  Object source, ServiceModule serviceModule, Long personId, CaseObject oldState,  CaseObject newState ) {
        super(source);
        this.serviceModule = serviceModule;
        this.personId = personId;
        this.oldState = oldState;
        this.newState = newState;
    }

    public ServiceModule getServiceModule() {
        return serviceModule != null ? serviceModule : ServiceModule.GENERAL;
    }

    public CaseObject getCaseObject () {
        return newState != null ? newState : oldState;
    }

    public CaseObject getNewState() {
        return newState;
    }

    public CaseObject getOldState() {
        return oldState;
    }

    public CaseComment getCaseComment() { return null; }

    @Override
    public Long getPersonId() {
        return personId;
    }

    public IssueCreateRequest getIssueCreateRequest() {
        return issueCreateRequest;
    }

    public void setIssueCreateRequest(IssueCreateRequest issueCreateRequest) {
        this.issueCreateRequest = issueCreateRequest;
    }

    public boolean isCreateEvent() {
        return isCreateEvent;
    }

    public void setCreateEvent(boolean createEvent) {
        isCreateEvent = createEvent;
    }

    @Override
    public Long getCaseObjectId() {
        CaseObject caseObject = getCaseObject();
        if(caseObject==null) return null;
        return caseObject.getId();
    }

    @Override
    public boolean isEagerEvent() {
        CaseObject caseObject = getCaseObject();
        if(caseObject==null) return false;
        return Objects.equals(En_ExtAppType.REDMINE.getCode(), caseObject.getExtAppType() );
    }

    @Override
    public String toString() {
        return "CaseObjectEvent{" +
                "caseObjectId=" + getCaseObjectId() +
                ", isEagerEvent=" + isEagerEvent() +
                ", oldState=" + asString( oldState ) +
                ", newState=" + asString( newState ) +
                ", personId=" + personId +

                '}';
    }

    private String asString( CaseObject caseObject ) {
        if(caseObject==null) return null;
        return "CaseObject{" +
                "id=" + caseObject.getId() +
                ", caseNumber=" + caseObject.getCaseNumber() +
                ", typeId=" + caseObject.getTypeId() +
                ", extId='" + caseObject.getExtId() + '\'' +
                '}';
    }


}
