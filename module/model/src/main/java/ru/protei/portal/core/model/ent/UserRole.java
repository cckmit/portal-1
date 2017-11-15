package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EntityOptionSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by michael on 16.06.16.
 */
@JdbcEntity(table = "user_role")
public class UserRole extends AuditableObject implements EntityOptionSupport {



    @JdbcId(name = "id")
    private Long id;

    @JdbcColumn(name = "role_code")
    private String code;

    @JdbcColumn(name = "role_info")
    private String info;

    @JdbcEnumerated(EnumType.STRING)
    @JdbcColumnCollection(separator = ",")
    private Set<En_Privilege> privileges;

    @JdbcEnumerated(EnumType.STRING)
    @JdbcColumnCollection(separator = ",")
    private  Set<En_Scope> scopes;


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

    public boolean hasScope( En_Scope scope ) {
        return scopes != null && scopes.contains( scope );
    }

    public void addScope(En_Scope scope) {
        if (scopes == null)
            scopes = new HashSet<>();

        scopes.add(scope);
    }

    public Set< En_Scope > getScopes() {
        return scopes;
    }

    public void setScopes( Set< En_Scope > scopes ) {
        this.scopes = scopes;
    }

    public static UserRole fromEntityOption( EntityOption entityOption){
        if(entityOption == null)
            return null;

        UserRole userRole = new UserRole(entityOption.getId());
        userRole.setCode(entityOption.getDisplayText());
        return userRole;
    }

    @Override
    public EntityOption toEntityOption() {
        return new EntityOption(this.code, this.id);
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

    public UserRole() {}

    public UserRole( Long id ) {
        this.id = id;
    }

    @Override
    public String toString () {
        return "UserRole{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", info='" + info + '\'' +
                ", privileges=" + privileges +
                ", scope=" + scopes +
                '}';
    }
}
