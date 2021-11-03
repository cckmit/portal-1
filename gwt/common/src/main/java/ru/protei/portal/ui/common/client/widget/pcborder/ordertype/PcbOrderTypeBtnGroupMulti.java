package ru.protei.portal.ui.common.client.widget.pcborder.ordertype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderType;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderTypeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.stream.Stream;

public class PcbOrderTypeBtnGroupMulti extends ToggleBtnGroupMulti<En_PcbOrderType> {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();
        Stream.of(En_PcbOrderType.values()).forEach(type -> {
            addBtn(lang.getName(type), type);
            setEnsureDebugId(type, DebugIdsHelper.PCB_ORDER_TYPE.byId(type.getId()));
        });
    }

    @Inject
    En_PcbOrderTypeLang lang;
}
