package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PersonEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeServiceAsync;
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

    public void subscribe( ModelSelector< EntityOption > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< EntityOption > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        employeeService.getEmployeeOptionList( new RequestCallback< List< EntityOption > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( List< EntityOption > options ) {
                list.clear();
                list.addAll( options );
                notifySubscribers();
            }
        } );
    }

    @Inject
    EmployeeServiceAsync employeeService;

    @Inject
    Lang lang;

    private List< EntityOption > list = new ArrayList<>();

    List< ModelSelector< EntityOption > > subscribers = new ArrayList<>();

}
