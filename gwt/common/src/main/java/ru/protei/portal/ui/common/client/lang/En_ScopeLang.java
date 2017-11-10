package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Scope;


public class En_ScopeLang {

    public String getName( En_Scope value ) {
        switch (value) {
            case ADMIN:
                return lang.scopeAdmin();
            case CUSTOMER:
                return lang.scopeCustomer();
            case SUPPORT:
                return lang.scopeSupport();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
