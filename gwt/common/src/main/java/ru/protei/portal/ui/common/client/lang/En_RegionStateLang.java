package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_RegionState;

/**
 * Названия статусов регионов
 */
public class En_RegionStateLang {

    public String getStateName(En_RegionState state){
        if(state == null)
            return lang.errUnknownResult();

        switch (state){
            case UNKNOWN: return lang.regionStateUnknown();
            case RIVAL: return lang.regionStateRival();
            case TALK: return lang.regionStateTalk();
            case PROJECTING: return lang.regionStateProjecting();
            case DEVELOPMENT: return lang.regionStateDevelopment();
            case DEPLOYMENT: return lang.regionStateDeployment();
            case SUPPORT: return lang.regionStateSupport();
            case SUPPORT_FINISHED: return lang.regionStateSupportFinished();
            default:
                return lang.errUnknownResult();
        }
    }

    @Inject
    Lang lang;

}
