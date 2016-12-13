package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.PersonEvents;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор сотрудников любой компании
 */
public class ContactButtonSelector extends ButtonSelector<PersonShortView> {

    @Event
    public void onPersonListChanged( PersonEvents.ChangePersonModel event ) {
        if( company!= null && event.company.getId().equals( company.getId() ) )
            updatePersons();
    }

    @Inject
    public void init() {
        setSearchEnabled( true );
        setSearchAutoFocus( true );
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

        if( defaultValue != null) {
            addOption( defaultValue, null );
        }

        persons.forEach( person -> addOption( person.getDisplayShortName(), person ) );

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
            if( defaultValue != null) {
                addOption( defaultValue, null );
            }
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
