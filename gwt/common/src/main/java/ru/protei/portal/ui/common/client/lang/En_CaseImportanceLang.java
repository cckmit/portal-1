package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;

/**
 * Критичность кейсов
 */
public class En_CaseImportanceLang {

    public String getImportanceName(En_ImportanceLevel importance){

        if(importance == null)
            return lang.errUnknownResult();

        switch (importance){
            case BASIC: return lang.basicImportance();
            case IMPORTANT: return lang.importantImportance();
            case CRITICAL: return lang.criticalImportance();
            case COSMETIC: return lang.cosmeticImportance();
            default:
                return lang.errUnknownResult();
        }
    }

    public String getImportanceIcon(En_ImportanceLevel importance) {
        if (importance == null) {
            return "fa fa-unknown";
        }
        switch (importance){
            case BASIC: return "importance-level basic";
            case IMPORTANT: return "importance-level important";
            case CRITICAL: return "importance-level critical";
            case COSMETIC: return "importance-level cosmetic";
            default: return "fa fa-unknown";
        }
    }
    @Inject
    Lang lang;

}
