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
            case "unknown": return lang.projectStateUnknown();
            case "marketing": return lang.projectStateMarketing();
            case "presale": return lang.projectStatePresale();
            case "projecting": return lang.projectStateProjecting();
            case "development": return lang.projectStateDevelopment();
            case "deployment": return lang.projectStateDeployment();
            case "support": return lang.projectStateSupport();
            case "finished": return lang.projectStateFinished();
            case "testing": return lang.projectStateTesting();
            case "canceled": return lang.projectStateCanceled();
            case "paused": return lang.projectStatePaused();
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

    private static final String ICON = "project-state ";
}
