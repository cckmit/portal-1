package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.EmployeeRegistrationServiceImpl;

public class EmployeeRegistrationProbationCuratorsEvent extends ApplicationEvent {
    public EmployeeRegistrationProbationCuratorsEvent( EmployeeRegistrationServiceImpl employeeRegistrationService, EmployeeRegistration employeeRegistration, Person curator ) {
        super( employeeRegistrationService);

        this.curator = curator;
        this.employeeRegistration = employeeRegistration;
    }

    public Person getCurator() {
        return curator;
    }

    public EmployeeRegistration getEmployeeRegistration() {
        return employeeRegistration;
    }


    private Person curator;
    private EmployeeRegistration employeeRegistration;

    @Override
    public String toString() {
        return "EmployeeRegistrationProbationCuratorsEvent{" +
                "curator=" + curator +
                ", employeeRegistration=" + employeeRegistration +
                '}';
    }
}
