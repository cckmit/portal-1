package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by michael on 16.06.16.
 */
@JdbcEntity(table = "user_login")
public class UserLogin extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "ulogin")
    private String ulogin;

    @JdbcColumn(name = "upass")
    private String upass;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcColumn(name = "lastPwdChange")
    private Date lastPwdChange;

    @JdbcColumn(name = "pwdExpired")
    private Date pwdExpired;

    @JdbcEnumerated(EnumType.ID)
    @JdbcColumn(name = "astate")
    private En_AdminState adminStateId;

    @JdbcColumn(name = "personId")
    private Long personId;

    @JdbcJoinedColumn( mappedColumn = "displayname", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "personId", remoteColumn = "id", sqlTableAlias = "p" ),
    })
    private String displayName;

    @JdbcJoinedColumn( mappedColumn = "displayShortName", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "personId", remoteColumn = "id", sqlTableAlias = "p" ),
    })
    private String displayShortName;

    @JdbcJoinedColumn( mappedColumn = "isfired", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "personId", remoteColumn = "id", sqlTableAlias = "p" ),
    })
    private boolean isFired;

    @JdbcJoinedColumn( mappedColumn = "company_id", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "personId", remoteColumn = "id", sqlTableAlias = "p" ),
    })
    private Long companyId;

    @JdbcJoinedColumn( mappedColumn = "cname", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "personId", remoteColumn = "id", sqlTableAlias = "p" ),
            @JdbcJoinPath( table = "company", localColumn = "company_id", remoteColumn = "id", sqlTableAlias = "c" )
    })
    private String companyName;

    @JdbcColumn(name = "authType")
    @JdbcEnumerated(EnumType.ID)
    private En_AuthType authType;

    @JdbcColumn(name = "info")
    private String info;

    @JdbcManyToMany( localLinkColumn = "login_id", remoteLinkColumn = "role_id", linkTable = "login_role_item" )
    private Set< UserRole > roles;

    @JdbcColumnCollection(name = "ipMaskAllow", separator = ",")
    private List<String> ipMaskAllow;

    public UserLogin () {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUlogin() {
        return ulogin;
    }

    public void setUlogin(String ulogin) {
        this.ulogin = ulogin;
    }

    public String getUpass() {
        return upass;
    }

    public void setUpass(String upass) {
        this.upass = upass;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastPwdChange() {
        return lastPwdChange;
    }

    public void setLastPwdChange(Date lastPwdChange) {
        this.lastPwdChange = lastPwdChange;
    }

    public Date getPwdExpired() {
        return pwdExpired;
    }

    public void setPwdExpired(Date pwdExpired) {
        this.pwdExpired = pwdExpired;
    }

    public int getAdminStateId() {
        return adminStateId.getId();
    }

    public void setAdminStateId(int adminStateId) {
        this.adminStateId = En_AdminState.find( adminStateId );
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getDisplayName () { return displayName; }

    public void setDisplayName ( String displayName ) { this.displayName = displayName; }

    public String getDisplayShortName() { return displayShortName; }

    public void setDisplayShortName(String displayShortName) { this.displayShortName = displayShortName; }

    public boolean isFired () { return isFired; }

    public void setFired ( boolean fired ) { isFired = fired; }

    public Long getCompanyId () { return companyId; }

    public void setCompanyId ( Long companyId ) { this.companyId = companyId; }

    public String getCompanyName () { return companyName; }

    public void setCompanyName ( String companyName ) { this.companyName = companyName; }

    public En_AuthType getAuthType() {
        return authType;
    }

    public void setAuthType( En_AuthType authType) {
        this.authType = authType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isLDAP_Auth () {
        return this.authType == En_AuthType.LDAP;
    }

    public Set< UserRole > getRoles() {
        return roles;
    }

    public void setRoles( Set< UserRole > roles ) {
        this.roles = roles;
    }

    public void setPerson(Person person) {
        if (person == null)
            return;

        personId = person.getId();
        displayName = person.getDisplayName();

        if (person.getCompanyId() == null || person.getCompany() == null)
            return;

        companyId = person.getCompanyId();
        companyName = person.getCompany().getCname();
    }

    public List<String> getIpMaskAllow() {
        return ipMaskAllow;
    }

    public void setIpMaskAllow(List<String> ipMaskAllow) {
        this.ipMaskAllow = ipMaskAllow;
    }

    @Override
    public String getAuditType() {
        return "UserLogin";
    }

    @Override
    public boolean equals(Object obj) {
        if (id != null) {
            return obj instanceof UserLogin && id.equals(((UserLogin) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserLogin{" +
                "id=" + id +
                ", ulogin='" + ulogin + '\'' +
                ", upass='" + upass + '\'' +
                ", created=" + created +
                ", lastPwdChange=" + lastPwdChange +
                ", pwdExpired=" + pwdExpired +
                ", adminStateId=" + adminStateId +
                ", personId=" + personId +
                ", displayName='" + displayName + '\'' +
                ", displayShortName='" + displayShortName + '\'' +
                ", isFired=" + isFired +
                ", companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", authType=" + authType +
                ", info='" + info + '\'' +
                ", roles=" + roles +
                ", ipMaskAllow=" + ipMaskAllow +
                '}';
    }

    public interface Fields {
        String ROLES = "roles";
    }
}
