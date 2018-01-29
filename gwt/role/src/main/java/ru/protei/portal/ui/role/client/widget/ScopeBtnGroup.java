package ru.protei.portal.ui.role.client.widget;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.ui.common.client.lang.En_ScopeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;


/**
 * Типы областей видимости задачика
 */
public class ScopeBtnGroup extends ToggleBtnGroup< En_Scope > {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();
        for ( En_Scope type : En_Scope.values() ) {
            addBtn( lang.getName( type ), type );
        }
    }

    @Inject
    En_ScopeLang lang;
}
