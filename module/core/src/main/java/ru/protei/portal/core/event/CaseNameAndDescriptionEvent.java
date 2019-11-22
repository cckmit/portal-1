package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.util.DiffResult;

import java.util.Objects;

public class CaseNameAndDescriptionEvent extends ApplicationEvent implements AbstractCaseEvent {

    private DiffResult<CaseNameAndDescriptionChangeRequest> nameAndDescription = new DiffResult<>();
    private Person person;
    private ServiceModule serviceModule;
    private En_ExtAppType extAppType;

    public CaseNameAndDescriptionEvent(
            Object source,
            CaseNameAndDescriptionChangeRequest oldState,
            CaseNameAndDescriptionChangeRequest newState,
            Person person,
            ServiceModule serviceModule,
            En_ExtAppType extAppType) {
        super(source);
        this.nameAndDescription.setInitialState(oldState);
        this.nameAndDescription.setNewState(newState);
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
        CaseNameAndDescriptionChangeRequest changeRequest = nameAndDescription.getNewState() == null ?
                nameAndDescription.getInitialState() :
                nameAndDescription.getNewState();
        if(changeRequest == null) return null;
        return changeRequest.getId();
    }

    public DiffResult<CaseNameAndDescriptionChangeRequest> getNameAndDescription() {
        return nameAndDescription;
    }

    @Override
    public boolean isEagerEvent() {
        return Objects.equals(En_ExtAppType.REDMINE, extAppType);
    }
}
