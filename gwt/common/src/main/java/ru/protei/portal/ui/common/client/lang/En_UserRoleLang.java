package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_UserRole;

/**
 * Роли учетной записи
 */
public class En_UserRoleLang {

    public String getName( En_UserRole value ) {
        switch (value)
        {
            case EMPLOYEE:
                return lang.roleEmployee();
            case CRM_ADMIN:
                return lang.roleCRM_Admin();
            case CRM_USER:
                return lang.roleCRM_User();
            case CRM_CLIENT:
                return lang.roleCRM_Client();
            case DN_ADMIN:
                return lang.roleDN_Admin();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
