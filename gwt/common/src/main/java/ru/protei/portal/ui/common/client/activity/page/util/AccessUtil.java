package ru.protei.portal.ui.common.client.activity.page.util;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.shared.model.Profile;

public class AccessUtil {

    public static boolean canUseExternalLink(Profile profile) {
        return profile.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW);
    }
}
