package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;

import java.util.Objects;

public class CaseNameAndDescriptionEvent extends ApplicationEvent implements AbstractCaseEvent {

    private CaseNameAndDescriptionChangeRequest newState;
    private CaseNameAndDescriptionChangeRequest oldState;
    private Person person;
    private ServiceModule serviceModule;
    private En_ExtAppType extAppType;

    public CaseNameAndDescriptionEvent(
            Object source,
            CaseNameAndDescriptionChangeRequest newState,
            CaseNameAndDescriptionChangeRequest oldState,
            Person person,
            ServiceModule serviceModule,
            En_ExtAppType extAppType) {
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
        CaseNameAndDescriptionChangeRequest changeRequest = getCaseNameAndDescriptionChangeRequest();
        if(changeRequest == null) return null;
        return changeRequest.getId();
    }

    public CaseNameAndDescriptionChangeRequest getNewState() {
        return newState;
    }

    public CaseNameAndDescriptionChangeRequest getOldState() {
        return oldState;
    }

    public CaseNameAndDescriptionChangeRequest getCaseNameAndDescriptionChangeRequest() {
        return newState != null ? newState : oldState;
    }

    @Override
    public boolean isEagerEvent() {
        return Objects.equals(En_ExtAppType.REDMINE, extAppType);
    }
}
