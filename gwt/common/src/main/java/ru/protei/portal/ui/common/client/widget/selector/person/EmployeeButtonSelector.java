package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор сотрудников домашней компании
 */
public class EmployeeButtonSelector extends ButtonSelector<PersonShortView> implements ModelSelector<PersonShortView> {

    @Inject
    public void init( EmployeeModel employeeModel ) {
        employeeModel.subscribe(this);
        setSearchEnabled( true );
        setSearchAutoFocus( true );

        setDisplayOptionCreator( value -> {
            if ( value == null ) {
                return new DisplayOption( defaultValue );
            }

            return new DisplayOption(
                    value.getDisplayShortName(),
                    value.isFired() ? "not-active" : "",
                    value.isFired() ? "fa fa-ban ban" : "");
        } );
    }

    @Override
    public void fillOptions(List<PersonShortView> persons){
        clearOptions();

        if(defaultValue != null) {
            addOption(null);
            setValue(null);
        }

        persons.forEach( this :: addOption );
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;
}
