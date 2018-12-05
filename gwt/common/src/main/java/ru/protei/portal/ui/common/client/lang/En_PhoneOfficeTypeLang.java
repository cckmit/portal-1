package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EmployeeEquipment;
import ru.protei.portal.core.model.dict.En_PhoneOfficeType;

public class En_PhoneOfficeTypeLang {
    public String getName( En_PhoneOfficeType phoneOfficeType) {
        if (phoneOfficeType == null)
            return "";

        switch (phoneOfficeType) {
            case LONG_DISTANCE:
                return lang.phoneOfficeTypeLongDistance();
            case INTERNATIONAL:
                return lang.phoneOfficeTypeInternational();
        }
        return lang.unknownField();
    }

    @Inject
    private Lang lang;
}
