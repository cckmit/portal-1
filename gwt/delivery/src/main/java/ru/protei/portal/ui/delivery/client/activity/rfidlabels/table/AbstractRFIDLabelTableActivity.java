package ru.protei.portal.ui.delivery.client.activity.rfidlabels.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractRFIDLabelTableActivity extends
        InfiniteLoadHandler<RFIDLabel>, InfiniteTableWidget.PagerListener,
        EditClickColumn.EditHandler<RFIDLabel>, RemoveClickColumn.RemoveHandler<RFIDLabel> {
}
