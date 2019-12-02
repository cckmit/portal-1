package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.util.DiffResult;

import java.util.Objects;

public class CaseNameAndDescriptionEvent extends ApplicationEvent implements AbstractCaseEvent {

    private Long caseObjectId;
    private DiffResult<String> name;
    private DiffResult<String> info;
    private Person person;
    private ServiceModule serviceModule;
    private En_ExtAppType extAppType;

    public CaseNameAndDescriptionEvent(
            Object source,
            Long caseObjectId,
            DiffResult<String> name,
            DiffResult<String> info,
            Person person,
            ServiceModule serviceModule,
            En_ExtAppType extAppType) {
        super(source);
        this.caseObjectId = caseObjectId;
        this.name = name;
        this.info = info;
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
        return caseObjectId;
    }

    @Override
    public boolean isEagerEvent() {
        return Objects.equals(En_ExtAppType.REDMINE, extAppType);
    }

    public DiffResult<String> getName() {
        return name;
    }

    public DiffResult<String> getInfo() {
        return info;
    }
}
