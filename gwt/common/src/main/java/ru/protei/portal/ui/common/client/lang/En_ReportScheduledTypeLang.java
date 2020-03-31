package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;

public class En_ReportScheduledTypeLang {

    @Inject
    public En_ReportScheduledTypeLang(Lang lang) {
        this.lang = lang;
    }

    public String getType(En_ReportScheduledType value) {
        if (value == null) {
            return lang.errUnknownResult();
        }
        switch (value) {
            case NONE: return lang.reportScheduledTypeNone();
            case DAILY: return lang.reportScheduledTypeDaily();
            case WEEKLY: return lang.reportScheduledTypeWeekly();
            default: return lang.unknownField();
        }
    }

    Lang lang;
}
