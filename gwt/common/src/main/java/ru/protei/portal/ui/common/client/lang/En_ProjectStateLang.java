package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;

/**
 * Названия статусов проектов
 */
public class En_ProjectStateLang {

    public String getStateName(CaseState state){
        if (state == null || state.getState() == null)
            return lang.errUnknownResult();

        switch (state.getState().toLowerCase()) {
            case "unknown": return lang.regionStateUnknown();
            case "marketing": return lang.regionStateMarketing();
            case "presale": return lang.regionStatePresale();
            case "projecting": return lang.regionStateProjecting();
            case "development": return lang.regionStateDevelopment();
            case "deployment": return lang.regionStateDeployment();
            case "support": return lang.regionStateSupport();
            case "finished": return lang.regionStateFinished();
            case "testing": return lang.regionStateTesting();
            case "canceled": return lang.regionStateCanceled();
            case "paused": return lang.regionStatePaused();
            default:
                return lang.errUnknownResult();
        }
    }

    public String getStateIcon(CaseState state){
        if (state == null || state.getState() == null)
            return "fa fa-unknown";

        switch (state.getState().toLowerCase()){
            case "unknown": return ICON + "unknown";
            case "marketing": return ICON + "marketing";
            case "presale": return ICON + "talk";
            case "projecting": return ICON + "projecting";
            case "development": return ICON + "development";
            case "deployment": return ICON + "deployment";
            case "support": return ICON + "support";
            case "finished": return ICON + "support-finished";
            case "testing": return ICON + "testing";
            case "canceled": return ICON + "canceled";
            case "paused": return ICON + "paused";
            default:
                return "fa fa-unknown";
        }
    }

    @Inject
    Lang lang;

    private static final String ICON = "region-state ";
}
