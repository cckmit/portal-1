package ru.protei.portal.ui.official.client.widget;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор амплуа
 */
public class AmpluaButtonSelector extends ButtonSelector< En_DevUnitPersonRoleType > {

    @Inject
    public void init( Lang lang, En_PersonRoleTypeLang roleTypeLang ) {
        setDisplayOptionCreator( value -> new DisplayOption( value == null ? lang.selectOfficialAmplua() : roleTypeLang.getName( value ) ));
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();

        addOption( null );
        for (En_DevUnitPersonRoleType roleType: En_DevUnitPersonRoleType.getAmpluaRoles()) {
            addOption( roleType );
        }
    }
}
