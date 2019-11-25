package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор сотрудников любой компании
 */
public class ContactButtonSelector extends ButtonSelector<PersonShortView> {

    @Inject
    public void init() {
        setSearchEnabled( true );
        setSearchAutoFocus( true );
        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getName() ) );
    }

    @Override
    public void setValue( PersonShortView value ) {
        if( contactModel.isPushing() ){
            deferred = value;
        }else
            super.setValue( value );
    }


    public void fillOptions( List<PersonShortView> persons ){
        clearOptions();

        if( defaultValue != null ) {
            addOption( null );
        }

        persons.forEach(this::addOption);

        super.setValue( deferred );
        deferred = null;
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void setFired ( Boolean value ) { this.fired = value; }

    public void updateCompany( Company company ){
        if( company == null ) {
            clearOptions();
            return;
        }
        this.company = company;
        updatePersons();
    }

    private void updatePersons(){
        contactModel.requestPersonList( company, fired, this::fillOptions );
    }

    @Inject
    ContactModel contactModel;

    PersonShortView deferred;

    private Company company;
    private String defaultValue;
    private Boolean fired;
}
