package ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ContractCalculationType;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class ContractCalculationTypeSelector extends FormPopupSingleSelector<ContractCalculationType> {

    @Inject
    public void init(ContractCalculationTypeModel model) {
        this.model = model;
        setAsyncModel(model);
        setItemRenderer(option -> option == null ? defaultValue : option.getName());
        setValidation(false);
        setSearchEnabled(true);
    }

    public void setOrganization(String organization) {
        model.setOrganization(organization);
        model.clean();
    }

    private ContractCalculationTypeModel model;
}
