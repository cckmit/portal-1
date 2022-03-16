package ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractCalculationType;
import ru.protei.portal.ui.common.client.lang.En_ContractCalculationTypeLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class ContractCalculationTypeSelector extends FormPopupSingleSelector<En_ContractCalculationType> {

    @Inject
    public void init() {
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
        setModel(elementIndex -> {
            if (size(values) <= elementIndex) return null;
            return values.get(elementIndex);
        });
        setSearchEnabled(false);
    }

    @Inject
    En_ContractCalculationTypeLang lang;

    private List<En_ContractCalculationType> values = Arrays.asList(En_ContractCalculationType.values());
}
