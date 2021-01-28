package ru.protei.portal.ui.common.client.widget.selector.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.Map;

/**
 * Селектор состояния региона
 */
public class RegionStateBtnGroupMulti extends ToggleBtnGroupMulti<En_RegionState> {

    @Inject
    public void init() {
        addStyleName( "status-group" );
    }

    public void fillButtons(Map<En_RegionState, String> iconsColorsMap) {
        clear();

        for ( En_RegionState state : En_RegionState.values() ) {
            addBtnWithIconAndTooltip(
                    lang.getStateIcon( state )+" fa-lg",
                    "btn btn-default",
                    lang.getStateName(state),
                    state,
                    iconsColorsMap.get(state),
                    null
            );
        }
    }

    @Inject
    En_RegionStateLang lang;
}