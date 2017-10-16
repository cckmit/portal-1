package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by michael on 16.06.16.
 */
@JdbcEntity(table = "user_role")
public class UserRole extends AuditableObject {

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

    public boolean hasPrivilege( En_Privilege privilege ){
        return privileges != null && privileges.contains( privilege );
    }

    public void addPrivilege(En_Privilege privilege) {
        if (privileges == null)
            privileges = new HashSet<>();

        privileges.add(privilege);
    }

    @Override
    public String getAuditType() {
        return "UserRole";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserRole) {
            Long oid = ((UserRole)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString () {
        return "UserRole{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", info='" + info + '\'' +
                ", privileges=" + privileges +
                '}';
    }
}
