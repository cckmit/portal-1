package ru.protei.portal.ui.delivery.client.activity.card.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Card;

public interface AbstractCardTableActivity extends InfiniteLoadHandler<Card>, InfiniteTableWidget.PagerListener {
}
