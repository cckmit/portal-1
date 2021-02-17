package ru.protei.portal.ui.common.client.widget.selector.contract.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.ui.common.client.lang.En_ContractTypeLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class ContractTypeSelector extends FormPopupSingleSelector<En_ContractType> {

    @Inject
    public void init() {
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
        setModel(elementIndex -> {
            if (size(values) <= elementIndex) return null;
            return values.get(elementIndex);
        });
    }

    @Inject
    En_ContractTypeLang lang;

    private List<En_ContractType> values = Arrays.asList(En_ContractType.values());
}
