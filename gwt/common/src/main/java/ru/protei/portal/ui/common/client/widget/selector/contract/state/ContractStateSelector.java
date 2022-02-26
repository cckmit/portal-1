package ru.protei.portal.ui.common.client.widget.selector.contract.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.ContractState;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.En_ContractStateLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import java.util.List;

import static ru.protei.portal.core.model.dict.ContractState.allContractStates;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class ContractStateSelector extends FormPopupSingleSelector<CaseState> {

    @Inject
    public void init() {
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value.getState()));
        setModel(elementIndex -> {
            if (size(values) <= elementIndex) return null;
            return values.get(elementIndex);
        });
    }

    @Inject
    En_ContractStateLang lang;

    private List<CaseState> values = allContractStates();
}
