package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор сотрудников домашней компании
 */
public class EmployeeButtonSelector extends ButtonSelector<EntityOption> implements ModelSelector<EntityOption> {

    @Inject
    public void init( EmployeeModel employeeModel ) {
        employeeModel.subscribe(this);
        setSearchEnabled( true );
        setSearchAutoFocus( true );
    }

    @Override
    public void fillOptions(List<EntityOption> persons){
        clearOptions();

        if(defaultValue != null) {
            addOption(defaultValue, null);
            setValue(null);
        }

        persons.forEach(option -> addOption(option.getDisplayText(), option));
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;

}
