package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;

/**
 * Критичность кейсов
 */
public class En_CaseImportanceLang {

    public String getImportanceName(En_ImportanceLevel importance){

        if(importance == null)
            throw new NullPointerException("importance can't be null");

        switch (importance){
            case BASIC: return lang.basicImportance();
            case IMPORTANT: return lang.importantImportance();
            case CRITICAL: return lang.criticalImportance();
            case COSMETIC: return lang.cosmeticImportance();
            default:
                throw new IllegalArgumentException("unknown importance level");
        }
    }

    @Inject
    Lang lang;

}
