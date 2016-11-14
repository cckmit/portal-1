package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор сотрудников домашней компании
 */
public class EmployeeButtonSelector extends ButtonSelector<Person> implements ModelSelector<Person> {

    @Inject
    public void init( EmployeeModel employeeModel ) {
        employeeModel.subscribe(this);
        setSearchEnabled( true );
        setSearchAutoFocus( true );
    }

    @Override
    public void fillOptions(List<Person> persons){
        clearOptions();

        if(defaultValue != null)
            addOption( defaultValue , null );

        persons.forEach(option -> addOption(option.getDisplayShortName(), option));
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;

}
