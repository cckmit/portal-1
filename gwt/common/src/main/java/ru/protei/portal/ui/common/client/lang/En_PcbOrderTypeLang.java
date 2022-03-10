package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderType;

public class En_PcbOrderTypeLang {

    public String getName(En_PcbOrderType state) {
        if (state == null) {
            return lang.errUnknownResult();
        }

        switch (state) {
            case CARD:
                return lang.pcbOrderTypeCard();
            case STENCIL:
                return lang.pcbOrderTypeStencil();
            case FRONT_PANEL:
                return lang.pcbOrderTypeFrontPanel();
            default:
                return state.name();
        }
    }

    @Inject
    Lang lang;
}
