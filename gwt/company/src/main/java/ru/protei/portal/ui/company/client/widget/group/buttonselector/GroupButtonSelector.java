package ru.protei.portal.ui.company.client.widget.group.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор списка групп компаний
 */
public class GroupButtonSelector extends ButtonSelector< EntityOption > implements SelectorWithModel< EntityOption > {

    @Inject
    public void init( GroupModel groupModel) {
        groupModel.subscribe( this );
        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getDisplayText() ) );
    }

    public void fillOptions( List< EntityOption > options ) {
        clearOptions();

        addOption( null );
        options.forEach( this :: addOption );
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;

}
