package ru.protei.portal.ui.delivery.client.widget.cardbatch.contractors;

import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.ui.delivery.client.widget.cardbatch.contractors.item.ContractorsSelectorItemModel;

public interface AbstractContractorsSelector {

    void onModelChanged(ContractorsSelectorItemModel model);

    void onRoleChanged(ContractorsSelectorItemModel model, En_PersonRoleType previous, En_PersonRoleType actual);
}
