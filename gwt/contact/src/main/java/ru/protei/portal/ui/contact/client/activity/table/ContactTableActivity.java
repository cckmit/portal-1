package ru.protei.portal.ui.contact.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PeriodicTaskService;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.contact.client.service.ContactServiceAsync;

import java.util.List;
import java.util.function.Consumer;

/**
 * Активность таблицы контактов
 */
public abstract class ContactTableActivity implements AbstractContactTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        view.resetFilter();
    }

    @Event
    public void onShow( ContactEvents.Show event ) {

        this.fireEvent( new AppEvents.InitPanelName( lang.contacts() ) );
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        requestContacts();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Override
    public void onItemClicked( Person value ) {
        Window.alert( "Clicked on contact!" );
    }

    @Override
    public void onEditClicked(Person value) {
        Window.alert( "Clicked on edit icon!" );
    }

    @Override
    public void onFilterChanged() {
        requestContacts();
    }

    private void requestContacts() {

        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }

        view.clearRecords();

        contactService.getContacts(view.searchPattern().getValue(), view.company().getValue(), view.showFired().getValue(),
                view.sortField().getValue(), view.sortDir().getValue(), new RequestCallback< List< Person > >() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errorGetList(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(List<Person> persons) {
                        fillViewHandler = taskService.startPeriodicTask( persons, fillViewer, 50, 50 );
                    }
                });
    }

    Consumer< Person > fillViewer = new Consumer< Person >() {
        @Override
        public void accept( Person person ) {
            view.addRecord( person );
        }
    };

    @Inject
    Lang lang;

    @Inject
    AbstractContactTableView view;

    @Inject
    ContactServiceAsync contactService;

    @Inject
    PeriodicTaskService taskService;

    PeriodicTaskService.PeriodicTaskHandler fillViewHandler;

    private AppEvents.InitDetails initDetails;
}
