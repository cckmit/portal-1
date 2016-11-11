package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;
import java.util.function.Consumer;

/**
 * Модель сотрудников любой компании
 */
public abstract class PersonModel implements Activity {

    public void requestPersonList(Company company, Consumer<List<Person>> fillOptionsAction){
        ContactQuery query = new ContactQuery(company, null, En_SortField.comp_name, En_SortDir.ASC);

        contactService.getContacts(
                query,
                new RequestCallback<List<Person>>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(List<Person> options) {
                        fillOptionsAction.accept(options);
                    }
                }
        );
    }


    @Inject
    ContactServiceAsync contactService;
}
