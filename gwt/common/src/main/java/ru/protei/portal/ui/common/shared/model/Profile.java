package ru.protei.portal.ui.common.shared.model;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.UserRole;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Клиентский профиль пользовател]
 */
public class Profile implements Serializable {

    private Set<UserRole> roles;

    private String login;

    private String name;

    private Long id;

    private Set<En_Privilege> privileges;

    private En_Scope scope;

    private Company company;

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles( Set<UserRole> roles ) {
        this.roles = roles;
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
}
