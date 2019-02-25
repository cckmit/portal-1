package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.service.EmployeeRegistrationServiceImpl;

import java.util.List;
import java.util.Map;

public class EmployeeRegistrationProbationNotificaionEvent extends ApplicationEvent {
    public <U, K> EmployeeRegistrationProbationNotificaionEvent( EmployeeRegistrationServiceImpl employeeRegistrationService, List<EmployeeRegistration> probationExpires, Map<Long, ContactInfo> contactInfoMap ) {
        super( employeeRegistrationService)
        this.probationExpires = probationExpires;
        this.contactInfoMap = contactInfoMap;
    }


    public List<EmployeeRegistration> getProbationExpires() {
        return probationExpires;
    }

    public Map<Long, ContactInfo> getContactInfoMap() {
        return contactInfoMap;
    }

    private List<EmployeeRegistration> probationExpires;
    private Map<Long, ContactInfo> contactInfoMap;
}
