package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderState;

public class En_PcbOrderStateLang {

    public String getStateName(En_PcbOrderState state) {
        if (state == null) {
            return lang.errUnknownResult();
        }

        switch (state) {
            case RECEIVED:
                return lang.pcbOrderStateReceived();
            case ACCEPTED:
                return lang.pcbOrderStateAccepted();
            case SENT:
                return lang.pcbOrderStateSent();
            default:
                return state.name();
        }
    }

    @Inject
    Lang lang;
}
