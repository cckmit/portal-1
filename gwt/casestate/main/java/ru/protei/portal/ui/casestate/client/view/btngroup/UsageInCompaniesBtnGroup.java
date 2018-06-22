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

        addBtnWithIcon( "fa fa-users m-l-10 text-success", "btn btn-white ", typeLang.getStateName( En_CaseStateUsageInCompanies.ALL ), En_CaseStateUsageInCompanies.ALL );
        addBtnWithIcon( "fa fa-user m-l-10 text-purple", "btn btn-white ", typeLang.getStateName( En_CaseStateUsageInCompanies.SELECTED ), En_CaseStateUsageInCompanies.SELECTED );
        addBtnWithIcon( "fa fa-ban m-l-10", "btn btn-white ", typeLang.getStateName( En_CaseStateUsageInCompanies.NONE ), En_CaseStateUsageInCompanies.NONE );
    }

    @Inject
    En_CaseStateUsageInCompaniesLang typeLang;
}
