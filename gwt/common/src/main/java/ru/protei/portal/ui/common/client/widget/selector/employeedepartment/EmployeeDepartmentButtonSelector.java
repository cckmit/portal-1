package ru.protei.portal.ui.common.client.widget.selector.employeedepartment;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class EmployeeDepartmentButtonSelector extends ButtonPopupSingleSelector<EntityOption> {

    public EmployeeDepartmentButtonSelector() {
        super();
        defaultValue = lang.selectValue();
        setItemRenderer( value -> value == null ? defaultValue : value.getDisplayText() );
    }
}
