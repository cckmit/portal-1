package ru.protei.portal.ui.common.client.widget.selector.region;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор регионов
 */
public class RegionButtonSelector extends ButtonSelector<EntityOption> implements SelectorWithModel<EntityOption> {

    @Inject
    public void init( RegionModel regionModel ) {
        setSelectorModel(regionModel);

        setSearchAutoFocus( true );
        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getDisplayText() ) );
    }

    @Override
    public void fillOptions(List<EntityOption> regions){
        clearOptions();

        if(defaultValue != null) {
            addOption(null);
            setValue(null);
        }

        regions.forEach(this::addOption);
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;
}
