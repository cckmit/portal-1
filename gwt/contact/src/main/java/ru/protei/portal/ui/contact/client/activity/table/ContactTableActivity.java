package ru.protei.portal.ui.contact.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;

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
    public void onEditClicked(Person value ) {
        //Window.alert( "Clicked on edit icon!" );
        fireEvent(ContactEvents.Edit.byId(value.getId()));
    }


    @Override
    public void onCreateClick() {
        fireEvent(ContactEvents.Edit.newItem(view.company().getValue()));
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

        ContactQuery query = new ContactQuery();
        query.setSearchString(view.searchPattern().getValue());
        if(view.company().getValue() != null)
            query.setCompanyId(view.company().getValue().getId());
        query.setFired(view.showFired().getValue() ? null : view.showFired().getValue());
        query.setSortField(view.sortField().getValue());
        query.setSortDir(view.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC);

        contactService.getContacts(query, new RequestCallback< List< Person > >() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
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
