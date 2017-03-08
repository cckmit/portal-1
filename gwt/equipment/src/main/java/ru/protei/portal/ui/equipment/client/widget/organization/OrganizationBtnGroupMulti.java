package ru.protei.portal.ui.equipment.client.widget.organization;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.OrganizationCodeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;
import ru.protei.portal.core.model.dict.En_OrganizationCode;


/**
 * Типы огранизаций оборудования НТЦ протей
 */
public class OrganizationBtnGroupMulti extends ToggleBtnGroupMulti< En_OrganizationCode > {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();

        for ( En_OrganizationCode code : En_OrganizationCode.values() ) {
            addBtn( codeLang.getCompanyName( code ), code );
        }
    }

    @Inject
    OrganizationCodeLang codeLang;
}
