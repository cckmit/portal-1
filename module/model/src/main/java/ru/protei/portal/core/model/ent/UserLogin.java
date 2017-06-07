package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by michael on 16.06.16.
 */
@JdbcEntity(table = "user_login")
public class UserLogin implements Serializable {

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

    @JdbcColumn(name = "astate")
    private int adminStateId;

/*
    @JdbcColumn(name = "roleId")
    private int roleId;
*/

    @JdbcColumn(name = "personId")
    private Long personId;

    @JdbcJoinedObject(localColumn = "personId", remoteColumn = "id", updateLocalColumn = false, sqlTableAlias = "p")
    private Person person;

    @JdbcColumn(name = "authType")
    private int authTypeId;

    @JdbcColumn(name = "info")
    private String info;


    public UserLogin () {

    }

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
        return adminStateId;
    }

    public void setAdminStateId(int adminStateId) {
        this.adminStateId = adminStateId;
    }

/*
    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
*/

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public int getAuthTypeId() {
        return authTypeId;
    }

    public void setAuthTypeId(int authTypeId) {
        this.authTypeId = authTypeId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isLDAP_Auth () {
        return this.authTypeId == En_AuthType.LDAP.getId();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson( Person person ) {
        this.person = person;
    }
}
