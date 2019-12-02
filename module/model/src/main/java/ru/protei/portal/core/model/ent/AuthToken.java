package ru.protei.portal.core.model.ent;

import java.util.Set;

/**
 * Авторизационный токен
 */
public class AuthToken {

    private String sid;
    private String ip;
    private Long expired;
    private Long userLoginId;
    private Long personId;
    private Long companyId;
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

    public Long getExpired() {
        return expired;
    }

    public void setExpired(Long expired) {
        this.expired = expired;
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

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "sid='" + sid + '\'' +
                ", ip='" + ip + '\'' +
                ", expired=" + expired +
                ", userLoginId=" + userLoginId +
                ", personId=" + personId +
                ", companyId=" + companyId +
                ", roles=" + roles +
                '}';
    }
}
