package ru.protei.portal.core.wsapi;

import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;

/**
 * Created by Mike on 03.05.2017.
 */
public class WSAPIDefs {

    public static final String WS_API_CREATOR_INFO = "ws-api";
    public static final String WS_API_APP_TYPE = "WSAPI";
    public static final String WS_API_PROVIDER_CODE = "DG-demo-provider";

    public static boolean testCaseVisible (CaseObject object) {
        return object != null && HelperFunc.isNotEmpty(object.getExtAppType()) && object.getExtAppType().equals(WS_API_APP_TYPE);
    }
}
