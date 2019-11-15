package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.CaseObjectInfo;

import java.util.Objects;

public class CaseObjectInfoEvent extends ApplicationEvent implements AbstractCaseEvent {

    private CaseObjectInfo newState;
    private CaseObjectInfo oldState;
    private Person person;
    private ServiceModule serviceModule;
    private En_ExtAppType extAppType;

    public CaseObjectInfoEvent(Object source, CaseObjectInfo newState, CaseObjectInfo oldState, Person person, ServiceModule serviceModule, En_ExtAppType extAppType) {
        super(source);
        this.newState = newState;
        this.oldState = oldState;
        this.person = person;
        this.serviceModule = serviceModule;
        this.extAppType = extAppType;
    }

    @Override
    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public Long getCaseObjectId() {
        CaseObjectInfo caseObjectInfo = getCaseObjectInfo();
        if(caseObjectInfo==null) return null;
        return caseObjectInfo.getId();
    }

    public CaseObjectInfo getCaseObjectInfo() {
        return newState != null ? newState : oldState;
    }

    @Override
    public boolean isEagerEvent() {
        return Objects.equals(En_ExtAppType.REDMINE, extAppType);
    }
}
