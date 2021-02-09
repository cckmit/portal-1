package ru.protei.portal.ui.contract.client.activity.date.table;

import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractContractDateTableActivity extends
        EditClickColumn.EditHandler<ContractDate>, RemoveClickColumn.RemoveHandler<ContractDate> {
    void onEditClicked(ContractDate value);
    void onRemoveClicked(ContractDate value);
}
