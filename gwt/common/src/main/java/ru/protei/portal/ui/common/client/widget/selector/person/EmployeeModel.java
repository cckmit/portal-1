package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PersonEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
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
        myId = event.profile.getId();

        refreshOptions( );
    }

    @Event
    public void onEmployeeListChanged( PersonEvents.ChangeEmployeeModel event ) {
        refreshOptions();
    }

    public void subscribe( ModelSelector< PersonShortView > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< PersonShortView > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {
        employeeService.getEmployeeViewList( new EmployeeQuery( false, false, true, null, null, En_SortField.person_full_name, En_SortDir.ASC ),
                new RequestCallback< List< PersonShortView > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List< PersonShortView > options ) {
                int value = options.indexOf( new PersonShortView("", myId, false ) );
                if ( value > 0 ) {
                    options.add(0, options.remove(value));
                }

                list.clear();
                list.addAll( options );
                notifySubscribers();
            }
        } );
    }

    @Inject
    EmployeeControllerAsync employeeService;

    @Inject
    Lang lang;

    private List< PersonShortView > list = new ArrayList<>();

    List< ModelSelector< PersonShortView > > subscribers = new ArrayList<>();

    Long myId;
}
