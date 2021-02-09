package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;

public class En_CaseCommentPrivacyTypeLang {

    public String getName(En_CaseCommentPrivacyType privacyType){

        if(privacyType == null) {
            return lang.errUnknownResult();
        }

        switch (privacyType){
            case PUBLIC: return lang.privacyTypePublic();
            case PRIVATE_CUSTOMERS: return lang.privacyTypePrivateCustomers();
            case PRIVATE: return lang.privacyTypePrivate();
            default:
                return lang.errUnknownResult();
        }
    }

    @Inject
    Lang lang;

}
