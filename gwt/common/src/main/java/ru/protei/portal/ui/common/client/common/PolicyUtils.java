package ru.protei.portal.ui.common.client.common;


import ru.protei.portal.core.model.dict.En_UserRole;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.shared.model.Profile;

/**
 * Класс управления параметрами доступа приложения
 */
public class PolicyUtils {

    public static boolean isAllowedDashboardTab( Profile profile ) {
        return !profile.getRoles().contains( En_UserRole.DN_ADMIN.getId() );
    }

    public static boolean isAllowedCompanyTab( Profile profile ) {
        return !profile.getRoles().contains( En_UserRole.DN_ADMIN.getId() );
    }

    public static boolean isAllowedProductTab( Profile profile ) {
        return !profile.getRoles().contains( En_UserRole.DN_ADMIN.getId() );
    }

    public static boolean isAllowedContactTab( Profile profile ) {
        return !profile.getRoles().contains( En_UserRole.DN_ADMIN.getId() );
    }

    public static boolean isAllowedIssueTab( Profile profile ) {
        return !profile.getRoles().contains( En_UserRole.DN_ADMIN.getId() );
    }

    public static boolean isAllowedEquipmentTab( Profile profile ) {
        return profile.getRoles().contains( En_UserRole.DN_ADMIN.getId() );
    }

    public static boolean isAllowedRegionTab( Profile profile ) {
        return !profile.getRoles().contains( En_UserRole.DN_ADMIN.getId() );
    }

    public static boolean isAllowedProjectTab( Profile profile ) {
        return !profile.getRoles().contains( En_UserRole.DN_ADMIN.getId() );
    }
}
