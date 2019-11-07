package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

/**
 * Created by michael on 04.05.17.
 */
public class CaseObjectEvent extends ApplicationEvent implements AbstractCaseEvent {

    private CaseObject newState;
    private CaseObject oldState;
    private Person person;
    private ServiceModule serviceModule;


    public CaseObjectEvent(  Object source, ServiceModule serviceModule, Person person, CaseObject oldState,  CaseObject newState ) {
        super(source);
        this.serviceModule = serviceModule;
        this.person = person;
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

    public Person getPerson() {
        return person;
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
                ", person=" + (person == null ? null : person.getId()) +

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
