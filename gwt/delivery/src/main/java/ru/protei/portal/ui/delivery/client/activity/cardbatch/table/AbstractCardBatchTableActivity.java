package ru.protei.portal.ui.delivery.client.activity.cardbatch.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

public interface AbstractCardBatchTableActivity extends ClickColumn.Handler<CardBatch>, InfiniteLoadHandler<CardBatch>,
        EditClickColumn.EditHandler<CardBatch>, InfiniteTableWidget.PagerListener {
}
