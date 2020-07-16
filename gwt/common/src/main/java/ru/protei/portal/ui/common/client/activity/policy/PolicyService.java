package ru.protei.portal.ui.common.client.activity.policy;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.shared.model.Profile;

import java.util.Objects;

/**
 * Сервис для работы с привилегиями
 */
public abstract class PolicyService implements Activity {
    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        profile = event.profile;
    }

    public boolean hasPrivilegeFor( En_Privilege privilege ) {
        if ( profile == null ) {
            return false;
        }
        return profile.hasPrivilegeFor( privilege );
    }

    public boolean hasSystemScopeForPrivilege( En_Privilege privilege ) {
        if ( profile == null ) {
            return false;
        }
        return profile.hasSystemScopeForPrivilege( privilege );
    }

    public boolean hasScopeForPrivilege(En_Privilege privilege, En_Scope scope) {
        if (profile == null) {
            return false;
        }
        return profile.hasScopeForPrivilege(privilege, scope);
    }

    public boolean hasAnyPrivilegeOf( En_Privilege... privileges ) {
        if ( profile == null ) {
            return false;
        }

        for ( En_Privilege privilege : privileges ) {
            if ( profile.hasPrivilegeFor( privilege ) ) {
                return true;
            }
        }

        return false;
    }

    public boolean hasEveryPrivilegeOf( En_Privilege... privileges ) {
        if ( profile == null ) {
            return false;
        }

        for ( En_Privilege privilege : privileges ) {
            if ( !profile.hasPrivilegeFor( privilege ) ) {
                return false;
            }
        }

        return true;
    }

    public Profile getProfile() {
        return profile;
    }

    public Company getUserCompany() {
        if ( profile == null ) {
            return null;
        }

        return profile.getCompany();
    }

    public boolean personBelongsToHomeCompany() {

        if (profile == null || profile.getCompany() == null || profile.getCompany().getCategory() == null) {
            return false;
        }

        return Objects.equals(En_CompanyCategory.HOME, profile.getCompany().getCategory());
    }

    @Event
    public void onLogout( AppEvents.Logout event ) {
        profile = null;
    }

    Profile profile;
}
