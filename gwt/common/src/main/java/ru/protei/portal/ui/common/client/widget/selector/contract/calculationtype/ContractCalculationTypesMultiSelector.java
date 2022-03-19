package ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ContractCalculationType;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ContractCalculationTypesMultiSelector extends InputPopupMultiSelector<ContractCalculationType> {

    @Inject
    public void init(Lang lang, ContractCalculationTypeModel model) {
        this.model = model;
        setAsyncModel(model);
        setItemRenderer(option -> option == null ? "" : option.getName());
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setSearchEnabled(true);
    }

    public void setOrganization(String organization) {
        model.setOrganization(organization);
        model.clean();
    }

    private ContractCalculationTypeModel model;
}
