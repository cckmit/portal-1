package ru.protei.portal.ui.common.client.common;

import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;

public class PrivacyTypeStyleProvider {

    public static String getIcon(En_CaseCommentPrivacyType privacyType) {
        if (privacyType == null) {
            return "privacy-level";
        }
        switch (privacyType){
            case PUBLIC: return "privacy-level fas fa-lock-open text-success";
            case PRIVATE_CUSTOMERS: return "privacy-level fas fa-unlock text-warning";
            case PRIVATE: return "privacy-level fas fa-lock text-danger";
            default: return "privacy-level";
        }
    }
}
