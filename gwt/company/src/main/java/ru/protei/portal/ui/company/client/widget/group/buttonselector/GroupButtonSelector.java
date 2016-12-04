package ru.protei.portal.ui.company.client.widget.group.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.company.client.widget.group.GroupModel;

import java.util.List;

/**
 * Селектор списка групп компаний
 */
public class GroupButtonSelector extends ButtonSelector< EntityOption > implements ModelSelector< EntityOption > {

    @Inject
    public void init( GroupModel groupModel) {
        groupModel.subscribe( this );
    }

    public void fillOptions( List< EntityOption > options ) {
        clearOptions();

        addOption( defaultValue == null? "" : defaultValue , null );
        options.forEach( option -> addOption( option.getDisplayText(),option ) );
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;

}
