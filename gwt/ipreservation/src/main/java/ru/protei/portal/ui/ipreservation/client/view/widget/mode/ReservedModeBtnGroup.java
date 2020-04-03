package ru.protei.portal.ui.ipreservation.client.view.widget.mode;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

/**
 * Режим резервирования IP-адресов
 */
public class ReservedModeBtnGroup extends ToggleBtnGroup<En_ReservedMode> {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();

        addBtn(lang.reservedIpExactIp(), En_ReservedMode.EXACT_IP, "btn btn-default");
        addBtn(lang.reservedIpAnyFreeIps(), En_ReservedMode.ANY_FREE_IPS, "btn btn-default");
        addBtn(lang.reservedIpSelectedSubnets(), En_ReservedMode.FROM_SELECTED_SUBNETS, "btn btn-default");
/*
        addBtnWithIcon( "fa fa-users m-l-10 text-complete", "btn btn-default ", typeLang.getStateName( En_CaseStateUsageInCompanies.ALL ), En_CaseStateUsageInCompanies.ALL );
        addBtnWithIcon( "fa fa-user m-l-10 text-purple", "btn btn-default ", typeLang.getStateName( En_CaseStateUsageInCompanies.SELECTED ), En_CaseStateUsageInCompanies.SELECTED );
        addBtnWithIcon( "fa fa-ban m-l-10", "btn btn-default ", typeLang.getStateName( En_CaseStateUsageInCompanies.NONE ), En_CaseStateUsageInCompanies.NONE );
*/
    }

    @Inject
    Lang lang;
}
