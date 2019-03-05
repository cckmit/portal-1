package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.EmployeeRegistrationServiceImpl;

public class EmployeeRegistrationProbationCuratorsEvent extends ApplicationEvent {
    public EmployeeRegistrationProbationCuratorsEvent( Object source, Person curator, String employeeFullName, Long employeeId ) {
        super( source );

        this.curator = curator;
        this.employeeFullName = employeeFullName;
        this.employeeId = employeeId;
    }

    public Person getCurator() {
        return curator;
    }

    public Long getEmployeeId() {

        return employeeId;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    @Override
    public String toString() {
        return "EmployeeRegistrationProbationCuratorsEvent{" +
                "curator=" + curator +
                ", employeeId=" + employeeId +
                ", employeeFullName='" + employeeFullName + '\'' +
                '}';
    }

    private Person curator;
    private Long employeeId;
    private String employeeFullName;
}
