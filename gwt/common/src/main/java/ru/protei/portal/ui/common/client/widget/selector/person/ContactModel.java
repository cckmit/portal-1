package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Модель сотрудников любой компании
 */
public abstract class ContactModel implements Activity {

    public void requestPersonList(Company company, Consumer<List<EntityOption>> fillOptionsAction){
        ContactQuery query = new ContactQuery();
        query.setCompanyId(company.getId());
        query.setSortDir(En_SortDir.ASC);
        query.setSortField(En_SortField.comp_name);

        isPushing = true;
        contactService.getContacts(
                query,
                new RequestCallback<List<Person>>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(List<Person> persons) {
                        List<EntityOption> entityOptions = new ArrayList<>(persons.size());
                        persons.forEach(person -> entityOptions.add(EntityOption.fromPerson(person)));
                        fillOptionsAction.accept(entityOptions);
                        isPushing = false;
                    }
                }
        );
    }


    public boolean isPushing(){
        return isPushing;
    }

    @Inject
    ContactServiceAsync contactService;

    private boolean isPushing;
}
