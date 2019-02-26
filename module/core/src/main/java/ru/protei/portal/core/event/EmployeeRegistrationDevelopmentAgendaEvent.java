package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.service.EmployeeRegistrationServiceImpl;

public class EmployeeRegistrationDevelopmentAgendaEvent extends ApplicationEvent {
    public EmployeeRegistrationDevelopmentAgendaEvent( EmployeeRegistrationServiceImpl employeeRegistrationService, String employeeName ) {
        super( employeeRegistrationService );
        this.employeeName = employeeName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    private String employeeName;
    private ContactInfo contactInfo;
}
