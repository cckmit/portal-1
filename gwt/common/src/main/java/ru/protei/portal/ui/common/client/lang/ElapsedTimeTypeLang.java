package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;

public class ElapsedTimeTypeLang {

    public String getName( En_TimeElapsedType elapsedType) {
        if (elapsedType == null)
            return "";
        switch (elapsedType) {
            case NONE:
                return lang.timeElapsedTypeNone();
            case WATCH:
                return lang.timeElapsedTypeWatch();
            case NIGHT_WORK:
                return lang.timeElapsedTypeNightWork();
        }
        return lang.unknownField();
    }

    @Inject
    Lang lang;
}
