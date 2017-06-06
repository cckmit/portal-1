package ru.protei.portal.ui.common.client.common;


import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.shared.model.Profile;

/**
 * Класс управления параметрами доступа приложения
 */
public class PolicyUtils {

    public static boolean isAllowedDashboardTab( Profile profile ) {
        for ( UserRole userRole : profile.getRoles() ) {
            if( !userRole.getCode().equals( DN_ADMIN_ROLE_CODE ) ) {
                return true;
            }
        }
        return false;
        //return !profile.getRoles().getCode().equals( DN_ADMIN_ROLE_CODE );
    }

    public static boolean isAllowedCompanyTab( Profile profile ) {
        for ( UserRole userRole : profile.getRoles() ) {
            if( !userRole.getCode().equals( DN_ADMIN_ROLE_CODE ) ) {
                return true;
            }
        }
        return false;
        //return !profile.getRole().getCode().equals( DN_ADMIN_ROLE_CODE );
    }

    public static boolean isAllowedProductTab( Profile profile ) {
        for ( UserRole userRole : profile.getRoles() ) {
            if( !userRole.getCode().equals( DN_ADMIN_ROLE_CODE ) ) {
                return true;
            }
        }
        return false;
        //return !profile.getRole().getCode().equals( DN_ADMIN_ROLE_CODE );
    }

    public static boolean isAllowedContactTab( Profile profile ) {
        for ( UserRole userRole : profile.getRoles() ) {
            if( !userRole.getCode().equals( DN_ADMIN_ROLE_CODE ) ) {
                return true;
            }
        }
        return false;
        //return !profile.getRole().getCode().equals( DN_ADMIN_ROLE_CODE );
    }

    public static boolean isAllowedIssueTab( Profile profile ) {
        for ( UserRole userRole : profile.getRoles() ) {
            if( !userRole.getCode().equals( DN_ADMIN_ROLE_CODE ) ) {
                return true;
            }
        }
        return false;
        //return !profile.getRole().getCode().equals( DN_ADMIN_ROLE_CODE );
    }

    public static boolean isAllowedEquipmentTab( Profile profile ) {
        for ( UserRole userRole : profile.getRoles() ) {
            if( userRole.getCode().equals( DN_ADMIN_ROLE_CODE ) ) {
                return true;
            }
        }
        return false;
        //return profile.getRole().getCode().equals( DN_ADMIN_ROLE_CODE );
    }

    public static boolean isAllowedRegionTab( Profile profile ) {
        for ( UserRole userRole : profile.getRoles() ) {
            if( !userRole.getCode().equals( DN_ADMIN_ROLE_CODE ) ) {
                return true;
            }
        }
        return false;
        //return !profile.getRole().getCode().equals( DN_ADMIN_ROLE_CODE );
    }

    public static boolean isAllowedProjectTab( Profile profile ) {
        for ( UserRole userRole : profile.getRoles() ) {
            if( !userRole.getCode().equals( DN_ADMIN_ROLE_CODE ) ) {
                return true;
            }
        }
        return false;
        //return !profile.getRole().getCode().equals( DN_ADMIN_ROLE_CODE );
    }

    private static final String DN_ADMIN_ROLE_CODE = "dn-admin";
}
