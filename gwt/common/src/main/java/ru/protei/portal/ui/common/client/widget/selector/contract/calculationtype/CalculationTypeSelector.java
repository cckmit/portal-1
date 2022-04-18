package ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CalculationType;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class CalculationTypeSelector extends FormPopupSingleSelector<CalculationType> {

    @Inject
    public void init(CalculationTypeModel model) {
        this.model = model;
        setAsyncModel(model);
        setSearchEnabled(true);
        setHasNullValue(true);
        setDefaultValue(lang.contractCalculationTypeNotDefined());
        setItemRenderer(option -> option == null ? defaultValue : option.getName());
    }

    public void setOrganization(String organization) {
        model.clean();
        model.setOrganization(organization);
    }

    private CalculationTypeModel model;
}
