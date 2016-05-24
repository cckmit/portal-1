package ru.protei.portal.core.model.ent;

/**
 *
 */

public class PersonRole {

    private Long id;
    private String roleName;

    public PersonRole() {
    }

    public Long getId() {
        return this.id;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}
