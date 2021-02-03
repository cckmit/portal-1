package ru.protei.portal.ui.contract.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.ui.common.client.lang.En_ContractDatesTypeLang;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class ContractDatesTypeSelector extends FormPopupSingleSelector<En_ContractDatesType> {

    @Inject
    public void init() {
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
        setModel(elementIndex -> {
            if (size(values) <= elementIndex) return null;
            return values.get(elementIndex);
        });
    }

    @Inject
    En_ContractDatesTypeLang lang;

    private List<En_ContractDatesType> values = Arrays.asList(En_ContractDatesType.values());
}
