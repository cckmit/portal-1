package ru.protei.portal.ui.account.client.widget.role;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.widget.optionlist.base.ModelList;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

import java.util.List;

/**
 * Список ролей
 */
public class RoleOptionList extends OptionList< UserRole > implements ModelList< UserRole > {

    @Inject
    public void init( RoleModel roleModel ) {
        roleModel.subscribe( this );
    }

    public void fillOptions( List< UserRole > userRoles ) {
        clearOptions();
        userRoles.forEach( option -> addOption( option.getCode(), option, "list-item" ) );
    }
}
