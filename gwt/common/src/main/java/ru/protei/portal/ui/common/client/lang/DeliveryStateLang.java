package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;

public class DeliveryStateLang {
    public String getName(CaseState value) {
        if (value.getId() == null) {
            return value.getState();
        }
        switch (value.getId().intValue()) {
            case 39:
                return lang.deliveryStatePreliminary();
            case 40:
                return lang.deliveryStatePreReserve();
            case 41:
                return lang.deliveryStateReserve();
            case 42:
                return lang.deliveryStateAssembly();
            case 43:
                return lang.deliveryStateTest();
            case 44:
                return lang.deliveryStateReady();
            case 45:
                return lang.deliveryStateSent();
            case 46:
                return lang.deliveryStateWork();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
