package ru.protei.portal.ui.common.client.widget.selector.dict;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.CommonServiceAsync;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Created by bondarenko on 10.11.16.
 */
public class PersonButtonSelector extends ButtonSelector<Person> {

//    @Event
//    public void onInit( AuthEvents.Success event ) {
//        requestOptions();
//    }

    @Inject
    public void init( ) {
        setSearchEnabled( false );
        setSearchAutoFocus( true );
        requestOptions();
    }


    private void requestOptions() {

        if(company == null)
            company = homeCompany;

        ContactQuery query = new ContactQuery(company, null, En_SortField.comp_name, En_SortDir.ASC);

        contactService.getContacts(
                query,
                new RequestCallback<List<Person>>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(List<Person> persons) {
                        fillOptions(persons);
                    }
                }

        );

    }

    private void fillOptions(List<Person> persons){
        clearOptions();
        persons.forEach(person -> addOption(person.getDisplayShortName(), person));
    }


    public void setCompany(Company company){
        this.company = company;
    }

    @Inject
    ContactServiceAsync contactService;

    private Company company;

    public static Company homeCompany;
    static {
        homeCompany = new Company();
        homeCompany.setId(1L);
    }

}
