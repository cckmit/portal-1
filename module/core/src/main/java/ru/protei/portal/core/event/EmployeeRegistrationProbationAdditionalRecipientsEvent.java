package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

import java.util.List;

public class EmployeeRegistrationProbationAdditionalRecipientsEvent extends ApplicationEvent {
    public EmployeeRegistrationProbationAdditionalRecipientsEvent(Object source, List<String> recipients, String employeeFullName, Long employeeId ) {
        super( source );
        this.recipients = recipients;
        this.employeeFullName = employeeFullName;
        this.employeeId = employeeId;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    private List<String> recipients;
    private Long employeeId;
    private String employeeFullName;
}
