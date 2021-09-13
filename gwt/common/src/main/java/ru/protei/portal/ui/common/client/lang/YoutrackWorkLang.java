package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;

public class YoutrackWorkLang {

    public String getTypeName(En_YoutrackWorkType type) {
        if (type == null) {
            return lang.unknownField();
        }
        switch (type) {
            case NIOKR: return lang.reportYoutrackWorkTypeNiokr();
            case NMA: return lang.reportYoutrackWorkTypeNma();
            default:
                return lang.unknownField();
        }
    }
        
    @Inject
    Lang lang;
}
