package ru.protei.portal.ui.common.client.widget.selector.contractor.contractor;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;

import java.util.List;

public abstract class ContractorModel extends BaseSelectorModel<Contractor> implements Activity {
    public void fill(List<Contractor> list) {
        updateElements(list);
    }
}
