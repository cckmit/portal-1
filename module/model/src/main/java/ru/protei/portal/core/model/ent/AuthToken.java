package ru.protei.portal.core.model.ent;

import java.util.Collection;
import java.util.Set;

/**
 * Авторизационный токен
 */
public class AuthToken {

    private String sid;
    private String ip;
    private Long userLoginId;
    private Long personId;
    private String personDisplayShortName;
    private Long companyId;
    private Collection<Long> companyAndChildIds;
    private Set<UserRole> roles;

    public AuthToken(String sid) {
        this.sid = sid;
    }

    public String getSessionId() {
        return sid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getUserLoginId() {
        return userLoginId;
    }

    public void setUserLoginId(Long userLoginId) {
        this.userLoginId = userLoginId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Collection<Long> getCompanyAndChildIds() {
        return companyAndChildIds;
    }

    public void setCompanyAndChildIds(Collection<Long> companyAndChildIds) {
        this.companyAndChildIds = companyAndChildIds;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public String getPersonDisplayShortName() {
        return personDisplayShortName;
    }

    public void setPersonDisplayShortName(String personDisplayShortName) {
        this.personDisplayShortName = personDisplayShortName;
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "sid='" + sid + '\'' +
                ", ip='" + ip + '\'' +
                ", userLoginId=" + userLoginId +
                '}';
    }
}
