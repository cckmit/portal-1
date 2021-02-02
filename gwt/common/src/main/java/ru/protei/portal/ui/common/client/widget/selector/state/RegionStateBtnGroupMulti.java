package ru.protei.portal.ui.common.client.widget.selector.state;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionBtnGroupStateModel;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.List;

/**
 * Селектор состояния региона
 */
public class RegionStateBtnGroupMulti extends ToggleBtnGroupMulti<CaseState> implements SelectorWithModel<CaseState> {

    @Inject
    public void init( RegionBtnGroupStateModel model ) {
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
        if ( state == null )
            return lang.errUnknownResult();

        return regionStateLang.getStateName( En_RegionState.forId( state.getId() ) );
    }

    public String getStateIcon( CaseState state ) {
        if ( state == null )
            return "fa fa-unknown";

        return regionStateLang.getStateIcon( En_RegionState.forId( state.getId() ) );
    }

    @Inject
    Lang lang;
    @Inject
    En_RegionStateLang regionStateLang;
}