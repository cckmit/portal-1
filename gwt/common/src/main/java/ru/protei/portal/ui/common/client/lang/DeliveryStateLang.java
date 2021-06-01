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
            case "pre_reserve": return lang.deliveryStatePreReserve();
            case "reserve": return lang.deliveryStateReserve();
            case "assembly": return lang.deliveryStateAssembly();
            case "test": return lang.deliveryStateTest();
            case "ready": return lang.deliveryStateReady();
            case "sent": return lang.deliveryStateSent();
            case "work": return lang.deliveryStateWork();
            default: return state.getState();
        }
    }

    @Inject
    Lang lang;
}
