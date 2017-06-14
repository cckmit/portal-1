package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PrivilegeAction;


public class En_PrivilegeActionLang {

    public String getName( En_PrivilegeAction value ) {
        switch (value) {
            case LOGIN:
                return lang.privilegeLogin();
            case VIEW:
                return lang.privilegeView();
            case EDIT:
                return lang.privilegeEdit();
            case CREATE:
                return lang.privilegeCreate();
            case REPORT:
                return lang.privilegeReport();
            case EXPORT:
                return lang.privilegeExport();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
