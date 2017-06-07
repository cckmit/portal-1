package ru.protei.portal.ui.account.client.widget.role;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_UserRole;
import ru.protei.portal.ui.common.client.lang.En_UserRoleLang;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

/**
 * Селектор ролей
 */
public class RoleMultiSelector extends MultipleInputSelector< En_UserRole > {

    @Inject
    public void init() {
        fillOptions();
        setAddName( "Добавить" );
    }

    public void fillOptions() {
        clearOptions();

        for ( En_UserRole role : En_UserRole.values() ) {
            addOption( userRoleLang.getName( role ), role );
        }

    }

    @Inject
    private En_UserRoleLang userRoleLang;

}
