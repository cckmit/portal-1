package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EmployeeEquipment;

public class En_EmployeeEquipmentLang {
    public String getName(En_EmployeeEquipment equipment) {
        if (equipment == null)
            return "";

        switch (equipment) {
            case TABLE:
                return lang.employeeEquipmentTable();
            case CHAIR:
                return lang.employeeEquipmentChair();
            case COMPUTER:
                return lang.employeeEquipmentComputer();
            case MONITOR:
                return lang.employeeEquipmentMonitor();
        }
        return lang.unknownField();
    }

    @Inject
    private Lang lang;
}
