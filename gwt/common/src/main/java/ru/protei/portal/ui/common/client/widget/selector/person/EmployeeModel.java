package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.PersonEvents;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель контактов домашней компании
 */
public abstract class EmployeeModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    @Event
    public void onEmployeeListChanged( PersonEvents.ChangeEmployeeModel event ) {
        refreshOptions();
    }

    public void subscribe( ModelSelector<Person> selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< Person > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        contactService.getEmployees(new RequestCallback<List<Person>>() {
            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onSuccess(List<Person> options) {
                list.clear();
                list.addAll(options);

                notifySubscribers();
            }
        });
    }

    @Inject
    ContactServiceAsync contactService;

    private List< Person > list = new ArrayList<>();

    List< ModelSelector< Person > > subscribers = new ArrayList<>();

}
