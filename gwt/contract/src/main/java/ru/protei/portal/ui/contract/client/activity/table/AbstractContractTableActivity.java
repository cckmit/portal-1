package ru.protei.portal.ui.contract.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.CopyClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

public interface AbstractContractTableActivity extends
        ClickColumn.Handler<Contract>, CopyClickColumn.CopyHandler<Contract>, EditClickColumn.EditHandler<Contract>,
        InfiniteLoadHandler<Contract>, InfiniteTableWidget.PagerListener {
}
