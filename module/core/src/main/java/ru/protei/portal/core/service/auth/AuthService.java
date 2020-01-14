package ru.protei.portal.core.service.auth;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserLogin;

/**
 * Created by michael on 29.06.16.
 */
public interface AuthService {

    /**
     * default time-to-live for application session in seconds
     * 3 days
     */
    public static final int DEF_APP_SESSION_LIVE_TIME = 60*60*24*3;

    Result<AuthToken> login(String appSessionID, String login, String pwd, String ip, String userAgent);

    Result<AuthToken> logout(AuthToken token, String ip, String userAgent);

    Result<UserLogin> getUserLogin(AuthToken token, Long userLoginId);

//    for tests only

    default void makeThreadAuthToken(UserLogin userLogin) {}

    default void resetThreadAuthToken() {}

    default AuthToken getAuthToken() {
        return null;
    }
}
