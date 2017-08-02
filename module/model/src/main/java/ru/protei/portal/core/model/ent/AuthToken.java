package ru.protei.portal.core.model.ent;

/**
 * Авторизационный токен
 */
public class AuthToken {
    public AuthToken(String sid, String ip) {
        this.sid = sid;
        this.ip = ip;
    }

    public String getSid() {
        return sid;
    }

    public String getIp() {
        return ip;
    }

    private String sid;

    private String ip;
}
