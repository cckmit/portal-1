package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.EmployeeRegistrationServiceImpl;

public class EmployeeRegistrationProbationHeadOfDepartmentEvent extends ApplicationEvent {
    public EmployeeRegistrationProbationHeadOfDepartmentEvent( EmployeeRegistrationServiceImpl employeeRegistrationService, EmployeeRegistration employeeRegistration, Person headOfDepartment ) {
        super( employeeRegistrationService);

        this.headOfDepartment = headOfDepartment;
        this.employeeRegistration = employeeRegistration;
    }

    public Person getHeadOfDepartment() {
        return headOfDepartment;
    }

    public EmployeeRegistration getEmployeeRegistration() {
        return employeeRegistration;
    }


    private Person headOfDepartment;
    private EmployeeRegistration employeeRegistration;

    @Override
    public String toString() {
        return "EmployeeRegistrationProbationHeadOfDepartmentEvent{" +
                "headOfDepartment=" + headOfDepartment +
                ", employeeRegistration=" + employeeRegistration +
                '}';
    }
}
