package ru.protei.portal.ui.casestate.client.view.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies;
import ru.protei.portal.ui.common.client.lang.En_CaseStateUsageInCompaniesLang;
import ru.protei.portal.ui.common.client.lang.En_EquipmentTypeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;


/**
 * Тип использования статуса в компаниях
 */
public class UsageInCompaniesBtnGroup extends ToggleBtnGroup<En_CaseStateUsageInCompanies> {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();
        for ( En_CaseStateUsageInCompanies type : En_CaseStateUsageInCompanies.values() ) {
            addBtnWithImage( "./images/eq_" + type.name().toLowerCase() + ".png", "btn btn-white btn-without-border eq-type", null, type, typeLang.getStateName( type ) );
        }
    }

    @Inject
    En_CaseStateUsageInCompaniesLang typeLang;
}
