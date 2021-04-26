package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryStatus;

public class En_DeliveryStatusLang {
    public String getName(En_DeliveryStatus value) {
        switch (value) {
            case PRELIMINARY:
                return lang.deliveryStatusPreliminary();
            case PRE_RESERVE:
                return lang.deliveryStatusPreReserve();
            case RESERVE:
                return lang.deliveryStatusReserve();
            case ASSEMBLY:
                return lang.deliveryStatusAssembly();
            case TEST:
                return lang.deliveryStatusTest();
            case READY:
                return lang.deliveryStatusReady();
            case SENT:
                return lang.deliveryStatusSent();
            case WORK:
                return lang.deliveryStatusWork();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
