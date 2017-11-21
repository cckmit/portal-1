package ru.protei.portal.ui.product.client.activity.list;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

/**
 * Created by bondarenko on 17.11.17.
 */
public interface AbstractProductTableActivity extends
        ClickColumn.Handler<DevUnit>, EditClickColumn.EditHandler< DevUnit >,
        InfiniteLoadHandler<DevUnit>, InfiniteTableWidget.PagerListener{
}
