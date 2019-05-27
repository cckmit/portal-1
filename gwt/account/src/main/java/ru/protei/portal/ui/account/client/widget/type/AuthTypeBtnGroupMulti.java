package ru.protei.portal.ui.account.client.widget.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.ui.common.client.lang.En_AuthTypeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

/**
 * Типы авторизации
 */
public class AuthTypeBtnGroupMulti extends ToggleBtnGroupMulti< En_AuthType > {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();

        for ( En_AuthType type : En_AuthType.values() ) {
            addBtnWithImage( "./images/auth_" + type.name().toLowerCase() + ".png", "btn btn-default no-border auth-type", null, type, typeLang.getName( type ) );
        }
    }

    @Inject
    En_AuthTypeLang typeLang;
}
