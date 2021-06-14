package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;

/**
 * Названия статусов поставок
 */
public class DeliveryStateLang {

    public String getStateName(CaseState state) {
        if (state == null || state.getState() == null) {
            return lang.errUnknownResult();
        }

        switch (state.getState().toLowerCase()) {
            case "preliminary": return lang.deliveryStatePreliminary();
            case "reservation": return lang.deliveryStateReservation();
            case "reserved": return lang.deliveryStateReserved();
            case "assembly": return lang.deliveryStateAssembly();
            case "testing": return lang.deliveryStateTesting();
            case "ready": return lang.deliveryStateReady();
            case "sent": return lang.deliveryStateSent();
            case "works": return lang.deliveryStateWorks();
            default: return state.getState();
        }
    }

    @Inject
    Lang lang;
}
