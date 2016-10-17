package ru.protei.portal.ui.company.client.widget.group.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор списка групп компаний
 */
public class GroupSelector extends ButtonSelector< CompanyGroup > implements ModelSelector< CompanyGroup > {

    @Inject
    public void init( GroupModel groupModel) {
        groupModel.subscribe( this );
    }

    public void fillOptions( List< CompanyGroup > groups ) {
        clearOptions();

        if ( hasAnyValue ) {
            addOption( lang.companyGroup(), null );
        }
        for ( CompanyGroup group : groups ) {
            addOption( group.getName(), group );
        }
    }

    public void setHasAnyValue( boolean hasAnyValue ) {
        this.hasAnyValue = hasAnyValue;
    }

    @Inject
    Lang lang;

    private boolean hasAnyValue = true;

}
