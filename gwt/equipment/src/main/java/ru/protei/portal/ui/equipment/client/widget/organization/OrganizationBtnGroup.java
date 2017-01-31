package ru.protei.portal.ui.equipment.client.widget.organization;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.OrganizationCodeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;
import ru.protei.portal.ui.common.shared.model.OrganizationCode;


/**
 * Типы огранизаций оборудования НТЦ протей
 */
public class OrganizationBtnGroup extends ToggleBtnGroup< OrganizationCode > {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();

        for ( OrganizationCode code : OrganizationCode.values() ) {
            addBtn( codeLang.getCompanyName( code ), code );
        }
    }

    @Inject
    OrganizationCodeLang codeLang;
}
