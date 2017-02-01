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

    public String getStateIcon( En_RegionState state ) {
        if(state == null)
            return "fa fa-unknown";

        switch (state){
            case UNKNOWN: return "region-state unknown";
            case RIVAL: return "region-state times";
            case TALK: return "region-state talk";
            case PROJECTING: return "region-state projecting";
            case DEVELOPMENT: return "region-state development";
            case DEPLOYMENT: return "region-state deployment";
            case SUPPORT: return "region-state support";
            case SUPPORT_FINISHED: return "region-state support-finished";
            default:
                return "fa fa-unknown";
        }
    }

    @Inject
    Lang lang;
}
