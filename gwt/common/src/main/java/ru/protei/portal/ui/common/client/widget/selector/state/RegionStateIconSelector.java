package ru.protei.portal.ui.common.client.widget.selector.state;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.icon.IconSelector;

/**
 * Селектор состояния региона
 */
public class RegionStateIconSelector extends IconSelector<En_RegionState> {

    @Inject
    public void init(En_RegionStateLang lang) {
        setDisplayOptionCreator( value -> new DisplayOption( lang.getStateName( value ), null, lang.getStateIcon( value ) ) );
        fillButtons();
    }

    @PostConstruct
    public void onConstruct() {
        addStyleName( "status-group" );
    }

    public void fillButtons() {
        clearOptions();

        for ( En_RegionState state : En_RegionState.values() ) {
            addOption( state );
        }
    }
}