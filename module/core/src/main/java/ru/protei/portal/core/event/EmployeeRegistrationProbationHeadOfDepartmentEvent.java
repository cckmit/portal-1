package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.EmployeeRegistrationServiceImpl;

public class EmployeeRegistrationProbationHeadOfDepartmentEvent extends ApplicationEvent {
    public EmployeeRegistrationProbationHeadOfDepartmentEvent( Object source, Person headOfDepartment, String employeeFullName, Long employeeId ) {
        super( source );

        this.headOfDepartment = headOfDepartment;
        this.employeeFullName = employeeFullName;
        this.employeeId = employeeId;
    }

    public Person getHeadOfDepartment() {
        return headOfDepartment;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    private Person headOfDepartment;
    private String employeeFullName;
    private Long employeeId;

    @Override
    public String toString() {
        return "EmployeeRegistrationProbationHeadOfDepartmentEvent{" +
                "headOfDepartment=" + headOfDepartment +
                ", employeeFullName='" + employeeFullName + '\'' +
                ", employeeId=" + employeeId +
                '}';
    }
}
