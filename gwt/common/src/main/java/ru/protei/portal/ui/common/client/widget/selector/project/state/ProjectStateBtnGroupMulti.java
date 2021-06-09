package ru.protei.portal.ui.common.client.widget.selector.project.state;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.lang.ProjectStateLang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.region.ProjectStateBtnGroupModel;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.List;

/**
 * Селектор состояния проекта
 */
public class ProjectStateBtnGroupMulti extends ToggleBtnGroupMulti<CaseState> implements SelectorWithModel<CaseState> {

    @Inject
    public void init( ProjectStateBtnGroupModel model ) {
        model.subscribe(this);
    }

    @PostConstruct
    public void onConstruct() {
        addStyleName( "status-group" );
    }

    public void fillOptions(List<CaseState> caseStates) {
        clear();

        for ( CaseState state : caseStates ) {
            addBtnWithIconAndTooltip(
                    getStateIcon( state ) + " fa-lg",
                    "btn btn-default",
                    getStateName( state ),
                    null,
                    state,
                    null,
                    state.getColor()
            );

            setEnsureDebugId(state, DebugIdsHelper.PROJECT_STATE.byId(state.getId()));
        }
    }

    public String getStateName( CaseState state ) {
        return projectStateLang.getStateName( state );
    }

    public String getStateIcon( CaseState state ) {
        return projectStateLang.getStateIcon( state );
    }

    @Inject
    ProjectStateLang projectStateLang;
}