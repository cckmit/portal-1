package ru.protei.portal.core.service.user;

/**
 * Created by michael on 29.06.16.
 */
public interface AuthService {

    /**
     * default time-to-live for application session in seconds
     * 3 hours
     */
    public static final int DEF_APP_SESSION_LIVE_TIME = 60*60*3;

    public UserSessionDescriptor findSession (String appSessionId, String ip, String userAgent);
    public AuthResult login (String appSessionID, String login, String pwd, String ip, String userAgent);
    public boolean logout (String appSessionId, String ip, String userAgent);
}
