package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryState;

public class En_DeliveryStateLang {
    public String getName(En_DeliveryState value) {
        switch (value) {
            case PRELIMINARY:
                return lang.deliveryStatePreliminary();
            case PRE_RESERVE:
                return lang.deliveryStatePreReserve();
            case RESERVE:
                return lang.deliveryStateReserve();
            case ASSEMBLY:
                return lang.deliveryStateAssembly();
            case TEST:
                return lang.deliveryStateTest();
            case READY:
                return lang.deliveryStateReady();
            case SENT:
                return lang.deliveryStateSent();
            case WORK:
                return lang.deliveryStateWork();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
