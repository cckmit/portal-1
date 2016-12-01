package ru.protei.portal.ui.common.shared.model;

import ru.protei.portal.core.model.ent.UserRole;

import java.io.Serializable;

/**
 * Клиентский профиль пользовател]
 */
public class Profile implements Serializable {

    private UserRole role;

    private String login;

    private String name;

    private Long id;

    public UserRole getRole() {
        return role;
    }

    public void setRole( UserRole role ) {
        this.role = role;
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

    public void setId( Long id ) {
        this.id = id;
    }
}
