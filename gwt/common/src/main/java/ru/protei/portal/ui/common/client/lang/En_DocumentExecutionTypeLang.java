package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentExecutionType;

/**
 * Названия типов документов
 */
public class En_DocumentExecutionTypeLang {

    public String getName(En_DocumentExecutionType type) {
        if (type == null) {
            return lang.errUnknownResult();
        }

        switch (type) {
            case ELECTRONIC: return lang.documentExecutionTypeElectronic();
            case PAPER: return lang.documentExecutionTypePaper();
            case TYPOGRAPHIC: return lang.documentExecutionTypeTypographic();
            default: return lang.errUnknownResult();
        }
    }

    @Inject
    Lang lang;
}
