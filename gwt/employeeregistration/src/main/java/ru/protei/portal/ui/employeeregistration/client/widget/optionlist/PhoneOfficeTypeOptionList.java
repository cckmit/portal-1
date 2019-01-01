package ru.protei.portal.ui.employeeregistration.client.widget.optionlist;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EmployeeEquipment;
import ru.protei.portal.core.model.dict.En_PhoneOfficeType;
import ru.protei.portal.ui.common.client.lang.En_EmployeeEquipmentLang;
import ru.protei.portal.ui.common.client.lang.En_PhoneOfficeTypeLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

public class PhoneOfficeTypeOptionList extends OptionList<En_PhoneOfficeType> {

    @Inject
    public void init( En_PhoneOfficeTypeLang lang) {
        for (En_PhoneOfficeType type : En_PhoneOfficeType.values())
            addOption(lang.getName(type), type);
    }
}
