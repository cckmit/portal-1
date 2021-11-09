package ru.protei.portal.ui.delivery.client.activity.pcborder.meta;

import ru.protei.portal.core.model.dict.En_PcbOrderType;

public interface AbstractPcbOrderMetaActivity {
    void onOrderTypeChanged(En_PcbOrderType value);
}
