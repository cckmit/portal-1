package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.CaseState;

/**
 * Названия статусов регионов
 */
public class En_RegionStateLang {

    public String getStateName(En_RegionState state){
        if(state == null)
            return lang.errUnknownResult();

        switch (state){
            case UNKNOWN: return lang.regionStateUnknown();
            case MARKETING: return lang.regionStateMarketing();
            case PRESALE: return lang.regionStatePresale();
            case PROJECTING: return lang.regionStateProjecting();
            case DEVELOPMENT: return lang.regionStateDevelopment();
            case DEPLOYMENT: return lang.regionStateDeployment();
            case SUPPORT: return lang.regionStateSupport();
            case FINISHED: return lang.regionStateFinished();
            case TESTING: return lang.regionStateTesting();
            case CANCELED: return lang.regionStateCanceled();
            case PAUSED: return lang.regionStatePaused();
            default:
                return lang.errUnknownResult();
        }
    }

    public String getStateName( CaseState state ) {
        if (state == null)
            return lang.errUnknownResult();

        return getStateName( En_RegionState.forId( state.getId() ) );
    }

    public String getStateIcon( En_RegionState state ) {
        if(state == null)
            return "fa fa-unknown";

        switch (state){
            case UNKNOWN: return "region-state unknown";
            case MARKETING: return "region-state marketing";
            case PRESALE: return "region-state talk";
            case PROJECTING: return "region-state projecting";
            case DEVELOPMENT: return "region-state development";
            case DEPLOYMENT: return "region-state deployment";
            case SUPPORT: return "region-state support";
            case FINISHED: return "region-state support-finished";
            case TESTING: return "region-state testing";
            case CANCELED: return "region-state canceled";
            case PAUSED: return "region-state paused";

            default:
                return "fa fa-unknown";
        }
    }

    public String getStateIcon( CaseState state ) {
        if (state == null)
            return "fa fa-unknown";

        return getStateIcon( En_RegionState.forId( state.getId() ) );
    }

    @Inject
    Lang lang;
}
