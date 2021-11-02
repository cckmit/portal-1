package ru.protei.portal.ui.delivery.client.activity.pcborder.table;

import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.widget.table.HasGroupFunctions;

public interface AbstractPcbOrderTableActivity extends ClickColumn.Handler<PcbOrder>,
        EditClickColumn.EditHandler<PcbOrder>,
        RemoveClickColumn.RemoveHandler<PcbOrder>, HasGroupFunctions<PcbOrder, PcbOrderGroupType> {
    void onFilterChange();
}
