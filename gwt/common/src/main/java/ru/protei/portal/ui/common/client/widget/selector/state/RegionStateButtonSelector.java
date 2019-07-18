package ru.protei.portal.ui.common.client.widget.selector.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор состояния региона
 */
public class RegionStateButtonSelector extends ButtonSelector<En_RegionState> {

    @Inject
    public void init( ) {
        setDisplayOptionCreator(value -> new DisplayOption(
                lang.getStateName( value ), "region-state-item", lang.getStateIcon(value) + " selector"));
        addBtnStyleName("region-state-btn");
        fillOptions();
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private void fillOptions() {
        if ( defaultValue != null ) {
            addOption( null );
        }
        for ( En_RegionState state : En_RegionState.values() ) {
            addOption( state );
        }
    }

    @Inject
    En_RegionStateLang lang;

    private String defaultValue;
}