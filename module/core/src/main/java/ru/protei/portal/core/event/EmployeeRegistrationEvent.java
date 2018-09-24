package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EmployeeRegistration;

public class EmployeeRegistrationEvent extends ApplicationEvent {
    private EmployeeRegistration employeeRegistration;

    public EmployeeRegistrationEvent(Object source, EmployeeRegistration employeeRegistration) {
        super(source);
        this.employeeRegistration = employeeRegistration;
    }

    public EmployeeRegistration getEmployeeRegistration() {
        return employeeRegistration;
    }

    public void setEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        this.employeeRegistration = employeeRegistration;
    }
}
