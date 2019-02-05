package ru.protei.portal.ui.contract.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.ui.common.client.columns.ClickColumn;

public interface AbstractContractTableActivity extends ClickColumn.Handler<Contract>,
        InfiniteLoadHandler<Contract> {
}
