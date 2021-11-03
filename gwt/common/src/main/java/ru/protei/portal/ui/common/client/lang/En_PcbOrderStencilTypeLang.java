package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_StencilType;

public class En_PcbOrderStencilTypeLang {

    public String getName(En_StencilType state) {
        if (state == null) {
            return lang.errUnknownResult();
        }

        switch (state) {
            case TOP:
                return lang.pcbOrderStencilTypeTop();
            case BOT:
                return lang.pcbOrderStencilTypeBot();
            case TOP_BOT:
                return lang.pcbOrderStencilTypeTopBot();
            default:
                return state.name();
        }
    }

    @Inject
    Lang lang;
}
