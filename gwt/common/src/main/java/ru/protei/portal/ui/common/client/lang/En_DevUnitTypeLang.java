package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;

public class En_DevUnitTypeLang {

    public String getName(En_DevUnitType value) {
        switch (value) {
            case COMPONENT:
                return lang.devUnitComponent();
            case PRODUCT:
                return lang.devUnitProduct();
            case DIRECTION:
                return lang.devUnitDirection();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
