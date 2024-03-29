package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.CaseObjectMeta;

import java.util.Objects;

public class CaseObjectMetaEvent extends ApplicationEvent implements AbstractCaseEvent {

    private CaseObjectMeta newState;
    private CaseObjectMeta oldState;
    private Long personId;
    private ServiceModule serviceModule;
    private En_ExtAppType extAppType;

    public CaseObjectMetaEvent(Object source, ServiceModule serviceModule, Long personId, En_ExtAppType extAppType, CaseObjectMeta oldState, CaseObjectMeta newState) {
        super(source);
        this.serviceModule = serviceModule;
        this.personId = personId;
        this.extAppType = extAppType;
        this.oldState = oldState;
        this.newState = newState;
    }

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public CaseObjectMeta getCaseObjectMeta() {
        return newState != null ? newState : oldState;
    }

    public CaseObjectMeta getNewState() {
        return newState;
    }

    public CaseObjectMeta getOldState() {
        return oldState;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getCaseObjectId() {
        CaseObjectMeta caseMeta = getCaseObjectMeta();
        if (caseMeta == null) return null;
        return caseMeta.getId();
    }

    @Override
    public boolean isEagerEvent() {
        return Objects.equals(En_ExtAppType.REDMINE, extAppType);
    }
}
