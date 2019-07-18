package ru.protei.portal.ui.common.shared.model;

import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.UserRole;

import java.io.Serializable;
import java.util.*;

/**
 * Клиентский профиль пользователя
 */
public class Profile implements Serializable {

    private Set<UserRole> roles;

    private String login;

    private String name;

    private String fullName;

    private Long id;

    private Long loginId;

    private int authTypeId;

    private Set<En_Privilege> privileges;

    private En_Gender gender;

    private Company company;

    private String shortName;

    private boolean fired;

    private Map<En_Privilege, Set<En_Scope>> privileges2scopes;

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles( Set<UserRole> roles ) {
        this.roles = roles;
        this.privileges2scopes = collectPrivilegeToScopeMap();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin( String login ) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public int getAuthTypeId() {
        return authTypeId;
    }

    public void setAuthTypeId(int authTypeId) {
        this.authTypeId = authTypeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setIsFired(boolean fired) {
        this.fired = fired;
    }

    public boolean isFired() {
        return fired;
    }

    public Set< En_Privilege > getPrivileges() {
        return privileges;
    }

    public void setPrivileges( Set< En_Privilege > privileges ) {
        this.privileges = privileges;
    }

    public boolean hasPrivilegeFor( En_Privilege privilege ) {
        if ( privileges == null ) {
            return false;
        }
        return privileges.contains( privilege );
    }

    public boolean hasGrantAccessFor( En_Privilege privilege ) {
        if ( privilege == null ) {
            return false;
        }

        Set<En_Scope> privilegeScopes = privileges2scopes.get( privilege );
        if (privilegeScopes == null || privilegeScopes.isEmpty()) {
            return false;
        }

        // В случае, если для привилегии установлена системная область видимости – предоставляем доступ без ограничений
        // Иначе если назначен scope в зависимости от entity или нет scope вообще – считаем что область видимости ограничена.
        return privilegeScopes.contains( En_Scope.SYSTEM );
    }

    public En_Gender getGender() {
        return gender;
    }

    public void setGender( En_Gender gender ) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Profile{" +
            "roles=" + roles +
            ", login='" + login + '\'' +
            ", name='" + name + '\'' +
            ", id=" + id +
            ", privileges=" + privileges +
            ", company=" + company +
            '}';
    }

    public void setCompany( Company company ) {
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    private Map<En_Privilege, Set<En_Scope>> collectPrivilegeToScopeMap() {
        if ( roles == null ) {
            return Collections.emptyMap();
        }

        Map<En_Privilege, Set<En_Scope>> privilegeToScope = new HashMap<>();
        for ( UserRole role : roles ) {
            if ( role.getPrivileges() == null || role.getScope() == null ) {
                continue;
            }

            for ( En_Privilege privilege : role.getPrivileges() ) {
                Set<En_Scope> scopes = privilegeToScope.computeIfAbsent(privilege, k -> new HashSet<>());
                scopes.add( role.getScope() );
            }
        }

        return privilegeToScope;
    }
}
