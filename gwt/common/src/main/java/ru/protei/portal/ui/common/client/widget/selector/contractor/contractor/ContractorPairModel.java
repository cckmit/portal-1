package ru.protei.portal.ui.common.client.widget.selector.contractor.contractor;

import ru.protei.portal.core.model.struct.ContractorPair;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;

import java.util.List;

public class ContractorPairModel extends BaseSelectorModel<ContractorPair> {
    public void fill(List<ContractorPair> list) {
        updateElements(list);
    }
}
