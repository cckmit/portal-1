package ru.protei.portal.ui.employeeregistration.client.widget.optionlist;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EmployeeEquipment;
import ru.protei.portal.ui.common.client.lang.En_EmployeeEquipmentLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

public class EmployeeEquipmentOptionList extends OptionList<En_EmployeeEquipment> {

    @Inject
    public void init(En_EmployeeEquipmentLang lang) {
        for (En_EmployeeEquipment equipment : En_EmployeeEquipment.values())
            addOption(lang.getName(equipment), equipment);
    }
}
