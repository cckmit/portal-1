package ru.protei.portal.ui.common.client.widget.selector.contractor.contractor;

import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;

import java.util.List;

public class ContractorModel extends BaseSelectorModel<Contractor> {
    public void fill(List<Contractor> list) {
        updateElements(list);
    }
}
