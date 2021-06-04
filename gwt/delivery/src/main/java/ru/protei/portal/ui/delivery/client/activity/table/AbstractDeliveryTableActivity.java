package ru.protei.portal.ui.delivery.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractDeliveryTableActivity extends
        ClickColumn.Handler<Delivery>, EditClickColumn.EditHandler<Delivery>, RemoveClickColumn.RemoveHandler<Delivery>,
        InfiniteLoadHandler<Delivery>, InfiniteTableWidget.PagerListener {

    void onFilterChanged();

    void onRemoveClicked(Delivery value);
}
