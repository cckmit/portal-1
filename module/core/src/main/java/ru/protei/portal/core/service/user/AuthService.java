package ru.protei.portal.core.service.user;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by michael on 29.06.16.
 */
public interface AuthService {

    /**
     * default time-to-live for application session in seconds
     * 3 days
     */
    public static final int DEF_APP_SESSION_LIVE_TIME = 60*60*24*3;

    public UserSessionDescriptor findSession (String appSessionId, String ip, String userAgent);
    public UserSessionDescriptor findSession (AuthToken token);
    public CoreResponse login (String appSessionID, String login, String pwd, String ip, String userAgent);
    public boolean logout (String appSessionId, String ip, String userAgent);
    public UserSessionDescriptor getUserSessionDescriptor(HttpServletRequest request);
}
