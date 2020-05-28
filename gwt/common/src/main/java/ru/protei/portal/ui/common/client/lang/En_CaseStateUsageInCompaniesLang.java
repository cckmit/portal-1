package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies;

/**
 * Использование статуса обращения в компаниях
 */
public class En_CaseStateUsageInCompaniesLang {

    public String getStateName(En_CaseStateUsageInCompanies state){
        if(state == null)
            return lang.errUnknownResult();

        switch (state){
            case NONE: return lang.caseStateUsagesInCompaniesNone();
            case ALL: return lang.caseStateUsagesInCompaniesAll();
            case SELECTED: return lang.caseStateUsagesInCompaniesSelected();
            default:
                return lang.errUnknownResult();
        }
    }

    @Inject
    Lang lang;

}
