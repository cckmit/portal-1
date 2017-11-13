package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Scope;


public class En_ScopeLang {

    public String getName( En_Scope value ) {
        switch (value) {
            case SYSTEM:
                return lang.scopeSystem();
            case COMPANY:
                return lang.scopeCompany();
            case LOCAL:
                return lang.scopeLocal();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
