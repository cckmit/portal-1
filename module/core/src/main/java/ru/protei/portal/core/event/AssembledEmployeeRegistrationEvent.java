package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import java.util.Objects;

public class AssembledEmployeeRegistrationEvent extends ApplicationEvent {
    private EmployeeRegistration oldState;
    private EmployeeRegistration newState;

    public AssembledEmployeeRegistrationEvent(Object source, EmployeeRegistration oldState, EmployeeRegistration newState) {
        super(source);
        this.oldState = oldState;
        this.newState = newState;
    }

    public boolean isEditEvent() {
        return oldState != null;
    }

    public boolean isEmploymentDateChanged() {
        return isEditEvent() && !Objects.equals(oldState.getEmploymentDate(), newState.getEmploymentDate());
    }

    public boolean isCuratorsChanged() {
        return isEditEvent() && !CollectionUtils.equals(oldState.getCuratorsIds(), newState.getCuratorsIds());
    }

    public DiffCollectionResult<Person> getCuratorsDiff() {
        DiffCollectionResult<Person> curatorsDiffs = new DiffCollectionResult<>();

        if (!isEditEvent()) {
            curatorsDiffs.putSameEntries(newState.getCurators());
            return curatorsDiffs;
        }

        return CollectionUtils.diffCollection(oldState.getCurators(), newState.getCurators());
    }

    public EmployeeRegistration getOldState() {
        return oldState;
    }

    public EmployeeRegistration getNewState() {
        return newState;
    }
}
