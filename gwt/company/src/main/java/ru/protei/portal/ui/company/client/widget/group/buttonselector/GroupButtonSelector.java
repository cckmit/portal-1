package ru.protei.portal.ui.company.client.widget.group.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.company.client.widget.group.GroupModel;

import java.util.List;

/**
 * Селектор списка групп компаний
 */
public class GroupButtonSelector extends ButtonSelector< CompanyGroup > implements ModelSelector< CompanyGroup > {

    @Inject
    public void init( GroupModel groupModel) {
        groupModel.subscribe( this );
    }

    public void fillOptions( List< CompanyGroup > groups ) {
        clearOptions();

        addOption( defaultValue == null? "" : defaultValue , null );
        for ( CompanyGroup group : groups ) {
            addOption( group.getName(), group );
        }
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;

}
