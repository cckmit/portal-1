package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;

public class EmployeeRegistrationDevelopmentAgendaEvent extends ApplicationEvent {
    public EmployeeRegistrationDevelopmentAgendaEvent( Object source, Person person ) {
        super( source );
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
