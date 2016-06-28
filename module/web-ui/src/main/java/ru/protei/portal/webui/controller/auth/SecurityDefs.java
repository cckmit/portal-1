package ru.protei.portal.webui.controller.auth;

/**
 * Created by michael on 23.06.16.
 */
public class SecurityDefs {

    public static final String AUTH_SESSION_DESC = "pctx-auth-session-data";
    public static final String APP_SESSION_ID_NAME= "portalsid";

    public static final String LOGIN_PAGE_URI = "/login.html";
    public static final String MAIN_WORKSPACE_URI = "/ws/";

    /**
     * default time-to-live for application session in seconds
     * 3 hours
     */
    public static final int DEF_APP_SESSION_LIVE_TIME = 60*60*3;

//    public static final String CLIENT_IP_REQ_ATTR = "pctx-client-ip";
//    public static final String CLIENT_SESSION_ID_ATTR = "pctx-session-id";
}
