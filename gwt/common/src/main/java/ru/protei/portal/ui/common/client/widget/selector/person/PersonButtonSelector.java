package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.events.PersonEvents;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор сотрудников любой компании
 */
public class PersonButtonSelector extends ButtonSelector<Person> {

    @Event
    public void onPersonListChanged( PersonEvents.ChangePersonModel event ) {
        if(company!= null && event.company.getId().equals(company.getId()))
            updatePersons();
    }

    @Inject
    public void init() {
        setSearchEnabled( true );
        setSearchAutoFocus( true );
    }

    public void fillOptions(List<Person> persons){
        clearOptions();

        if(defaultValue != null)
            addOption( defaultValue , null );

        persons.forEach(option -> addOption(option.getDisplayShortName(), option));
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void updateCompany(Company company){
        if(company == null) {
            clearOptions();
            return;
        }
        this.company = company;
        updatePersons();
    }

    private void updatePersons(){
        personModel.requestPersonList(company, this::fillOptions);
    }

    @Inject
    PersonModel personModel;

    private Company company;
    private String defaultValue;

}
