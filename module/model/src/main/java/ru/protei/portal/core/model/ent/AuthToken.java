package ru.protei.portal.core.model.ent;

import java.util.Set;

/**
 * Авторизационный токен
 */
public class AuthToken {

    private String ip;
    private String sid;
    private Long userLoginId;
    private Long personId;
    private Long companyId;
    private Set<UserRole> roles;

    public AuthToken(String sid, String ip, Long userLoginId, Long personId, Long companyId, Set<UserRole> roles) {
        this.sid = sid;
        this.ip = ip;
        this.userLoginId = userLoginId;
        this.personId = personId;
        this.companyId = companyId;
        this.roles = roles;
    }

    public String getSessionId() {
        return sid;
    }

    public String getIp() {
        return ip;
    }

    public Long getUserLoginId() {
        return userLoginId;
    }

    public Long getPersonId() {
        return personId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "ip='" + ip + '\'' +
                ", sid='" + sid + '\'' +
                ", userLoginId=" + userLoginId +
                ", personId=" + personId +
                ", companyId=" + companyId +
                ", roles=" + roles +
                '}';
    }
}
