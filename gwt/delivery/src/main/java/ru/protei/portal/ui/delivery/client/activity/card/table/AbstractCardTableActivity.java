package ru.protei.portal.ui.delivery.client.activity.card.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractCardTableActivity extends
        InfiniteLoadHandler<Card>, InfiniteTableWidget.PagerListener,
        ClickColumn.Handler<Card>, RemoveClickColumn.RemoveHandler<Card> {
}
