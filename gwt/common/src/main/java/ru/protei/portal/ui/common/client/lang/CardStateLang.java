package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;

/**
 * Названия статусов карт
 */
public class CardStateLang {

    public String getStateName(CaseState state) {
        if (state == null || state.getState() == null) {
            return lang.errUnknownResult();
        }

        switch (state.getState().toLowerCase()) {
            case "testing": return lang.cardStateTesting();
            case "in-stock": return lang.cardStateInStock();
            case "internal-use": return lang.cardStateInternalUse();
            case "reservation": return lang.cardStateReservation();
            case "sent": return lang.cardStateSent();
            case "repair": return lang.cardStateRepair();
            case "write-off": return lang.cardStateWriteOff();
            default: return state.getState();
        }
    }

    @Inject
    Lang lang;
}
