package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderPromptness;

public class En_PcbOrderPromptnessLang {

    public String getName(En_PcbOrderPromptness state) {
        if (state == null) {
            return lang.errUnknownResult();
        }

        switch (state) {
            case REGULAR:
                return lang.pcbOrderPromptnessRegular();
            case URGENT:
                return lang.pcbOrderPromptnessUrgent();
            case VERY_URGENT:
                return lang.pcbOrderPromptnessVeryUrgent();
            default:
                return state.name();
        }
    }

    @Inject
    Lang lang;
}
