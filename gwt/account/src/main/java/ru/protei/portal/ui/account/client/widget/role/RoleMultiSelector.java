package ru.protei.portal.ui.account.client.widget.role;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.optionlist.base.ModelList;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

/**
 * Мультиселектор ролей
 */
public class RoleMultiSelector
        extends MultipleInputSelector<UserRole>
        implements ModelList<UserRole >
{
    @Inject
    public void init( RoleModel model, Lang lang ) {
        model.subscribe( this );
        setAddName( lang.roleAdd() );
    }

    @Override
    public void fillOptions( List< UserRole > options ) {
        clearOptions();

        for ( UserRole type : options ) {
            addOption( type.getCode(), type );
        }
    }
}
