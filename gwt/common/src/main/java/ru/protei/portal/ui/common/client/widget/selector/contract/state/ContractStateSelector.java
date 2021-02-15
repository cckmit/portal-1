package ru.protei.portal.ui.common.client.widget.selector.contract.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.ui.common.client.lang.En_ContractStateLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class ContractStateSelector extends FormPopupSingleSelector<En_ContractState> {

    @Inject
    public void init() {
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
        setModel(elementIndex -> {
            if (size(values) <= elementIndex) return null;
            return values.get(elementIndex);
        });
    }

    @Inject
    En_ContractStateLang lang;

    private List<En_ContractState> values = Arrays.asList(En_ContractState.values());
}
