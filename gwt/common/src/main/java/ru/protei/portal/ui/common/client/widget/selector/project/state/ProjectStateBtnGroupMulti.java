package ru.protei.portal.ui.common.client.widget.selector.project.state;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.En_ProjectStateLang;
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
                    state,
                    state.getColor(),
                    null
            );
        }
    }

    public String getStateName( CaseState state ) {
        return regionStateLang.getStateName( state );
    }

    public String getStateIcon( CaseState state ) {
        return regionStateLang.getStateIcon( state );
    }

    @Inject
    En_ProjectStateLang regionStateLang;
}