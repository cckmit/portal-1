package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

/**
 * Связка логин-роль
 */
@JdbcEntity(table = "login_role_item")
public class LoginRoleItem implements Serializable {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "login_id")
    private long loginId;

    @JdbcColumn(name = "role_id")
    private int roleId;

    public LoginRoleItem () {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getLoginId() {
        return loginId;
    }

    public void setLoginId( long loginId ) {
        this.loginId = loginId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId( int roleId ) {
        this.roleId = roleId;
    }
}
