package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;

public class En_DeliveryAttributeLang {
    public String getName(En_DeliveryAttribute value) {
        switch (value) {
            case DELIVERY:
                return lang.deliveryAttributeDelivery();
            case TEST:
                return lang.deliveryAttributeTest();
            case PILOT_ZONE:
                return lang.deliveryAttributePilotZone();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
