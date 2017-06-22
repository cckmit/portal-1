package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_AuthType;

/**
 * Тип авторизации
 */
public class En_AuthTypeLang {
    public String getName( En_AuthType value ) {
        switch (value) {
            case LOCAL:
                return lang.accountLocal();
            case LDAP:
                return lang.accountLDAP();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
