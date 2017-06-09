package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by michael on 16.06.16.
 */
@JdbcEntity(table = "user_role")
public class UserRole implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private int id;

    @JdbcColumn(name = "role_code")
    private String code;

    @JdbcColumn(name = "ca_role_name")
    private String caRoleName;


    public UserRole() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCaRoleName() {
        return caRoleName;
    }

    public void setCaRoleName(String caRoleName) {
        this.caRoleName = caRoleName;
    }

    @Override
    public boolean equals( Object obj ) {

        if( !( obj instanceof UserRole ) )
            return false;

        UserRole ur = ( UserRole ) obj;

        return this.id == ur.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }
}
