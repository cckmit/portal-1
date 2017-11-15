package ru.protei.portal.ui.role.client.widget;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.ui.common.client.lang.En_EquipmentTypeLang;
import ru.protei.portal.ui.common.client.lang.En_ScopeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;


/**
 * Типы областей видимости задачика
 */
public class ScopeBtnGroupMulti extends ToggleBtnGroupMulti< En_Scope > {

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
