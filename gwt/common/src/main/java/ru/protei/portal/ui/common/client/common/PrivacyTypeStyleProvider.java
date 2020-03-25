package ru.protei.portal.ui.common.client.common;

import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;

public class PrivacyTypeStyleProvider {

    public static String getIcon(En_CaseCommentPrivacyType privacyType) {
        if (privacyType == null) {
            return "privacy-level";
        }
        switch (privacyType){
            case PUBLIC: return "privacy-level public fas fa-lock-open";
            case PRIVATE_CUSTOMERS: return "privacy-level private-customer fas fa-lock";
            case PRIVATE: return "privacy-level private fas fa-lock";
            default: return "privacy-level";
        }
    }
}
