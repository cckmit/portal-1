package ru.protei.portal.ui.contract.client.activity.table.concise;

import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

public interface AbstractContractConciseTableActivity extends
        ClickColumn.Handler<Contract>, EditClickColumn.EditHandler<Contract> {
}
