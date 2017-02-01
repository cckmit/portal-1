package ru.protei.portal.ui.common.client.widget.selector.state;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

/**
 * Селектор состояния региона
 */
public class RegionStateBtnGroup extends ToggleBtnGroup<En_RegionState> {

    @Inject
    public void init() {
        fillButtons();
    }

    @PostConstruct
    public void onConstruct() {
        addStyleName( "status-group" );
    }

    public void fillButtons() {
        clear();

        for ( En_RegionState state : En_RegionState.values() ) {
            addBtnWithIcon( lang.getStateIcon( state )+" fa-lg", "btn btn-white", null, state );
        }
    }

    @Inject
    En_RegionStateLang lang;
}