package ru.protei.portal.ui.common.client.widget.selector.contract.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.ContractStateLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class ContractStateSelector extends FormPopupSingleSelector<CaseState> {

    @Inject
    public void init(ContractStateModel model) {
        setAsyncModel(model);
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value.getState()));
    }

    @Inject
    ContractStateLang lang;
}
