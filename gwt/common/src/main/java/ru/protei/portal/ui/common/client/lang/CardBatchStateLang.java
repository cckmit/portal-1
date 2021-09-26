package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;

public class CardBatchStateLang {

    public String getStateName(CaseState state) {
        if (state == null || state.getState() == null) {
            return lang.errUnknownResult();
        }

        switch (state.getState().toLowerCase()) {
            case "preliminary": return lang.cardBatchStatePreliminary();
            case "actual": return lang.cardBatchStateActual();
            case "ordered": return lang.cardBatchStateOrdered();
            case "reserved": return lang.cardBatchStateReserved();
            case "mounting": return lang.cardBatchStateMounting();
            case "mounted": return lang.cardBatchStateMounted();
            default: return state.getState();
        }
    }

    @Inject
    Lang lang;
}
