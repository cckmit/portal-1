package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Set;

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

    @JdbcEnumerated(EnumType.STRING)
    @JdbcColumnCollection(name = "privileges", separator = ",")
    private Set<En_Privilege> privileges;

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

    public String getInfo() {
        return info;
    }

    public void setInfo( String info ) {
        this.info = info;
    }

    public Set<En_Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges( Set<En_Privilege> privileges ) {
        this.privileges = privileges;
    }
}
