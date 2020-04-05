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

        addBtn(lang.reservedIpExactIp(), En_ReservedMode.EXACT_IP, "btn btn-default col-md-6");
        addBtn(lang.reservedIpAnyFreeIps(), En_ReservedMode.ANY_FREE_IPS, "btn btn-default col-md-6");
    }

    @Inject
    Lang lang;
}
