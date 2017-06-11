package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

/**
 * Created by michael on 16.06.16.
 */
@JdbcEntity(table = "user_role")
public class UserRole implements Serializable {

    @JdbcId(name = "id")
    private Long id;

    @JdbcColumn(name = "role_code")
    private String code;

    @JdbcColumn(name = "role_info")
    private String info;

    @JdbcColumn(name = "ca_role_name")
    private String caRoleName;


    public UserRole() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getInfo() {
        return info;
    }

    public void setInfo( String info ) {
        this.info = info;
    }
}
