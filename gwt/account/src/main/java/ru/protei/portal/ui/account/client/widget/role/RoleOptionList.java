package ru.protei.portal.ui.account.client.widget.role;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_UserRole;
import ru.protei.portal.ui.common.client.lang.En_UserRoleLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

/**
 * Список ролей
 */
public class RoleOptionList extends OptionList<En_UserRole > {
    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clearOptions();
        for ( En_UserRole value : En_UserRole.values() ) {
            addOption( lang.getName( value ), value, "list-item" );
        }
    }

    @Inject
    En_UserRoleLang lang;
}
