package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.EmployeeRegistrationServiceImpl;

public class EmployeeRegistrationEmployeeFeedbackEvent extends ApplicationEvent {
    public EmployeeRegistrationEmployeeFeedbackEvent( EmployeeRegistrationServiceImpl employeeRegistrationService, Person person ) {
        super( employeeRegistrationService );
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    private Person person;

    @Override
    public String toString() {
        return "EmployeeRegistrationDevelopmentAgendaEvent{" +
                "person='" + person + '\'' +
                '}';
    }
}
