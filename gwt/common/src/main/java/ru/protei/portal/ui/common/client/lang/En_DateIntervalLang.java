package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;

/**
 * Названия типов временных интервалов
 */
public class En_DateIntervalLang {

    public String getName(En_DateIntervalType type) {
        switch (type) {
            case MONTH: return lang.monthInterval();
            case FIXED: return lang.fixedInterval();
            case UNLIMITED: return lang.unlimitedInterval();
            default: return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
