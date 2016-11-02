package ru.protei.portal.ui.contact.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.contact.client.service.ContactServiceAsync;

import java.util.List;

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

        initContacts();
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
    public void onEditClick(Person value ) {
        //Window.alert( "Clicked on edit icon!" );
        fireEvent(new ContactEvents.Edit(value.getId()));
    }


    @Override
    public void onCreateClick() {
        fireEvent(new ContactEvents.Edit());
    }

    @Override
    public void onFilterChanged() {
        initContacts();
    }

    private void initContacts() {

        contactService.getContacts(view.searchPattern().getValue(), view.company().getValue(), view.showFired().getValue() ? 1 : 0,
                view.sortField().getValue(), view.sortDir().getValue(), new RequestCallback< List< Person > >() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errorGetList(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(List<Person> persons) {
                        view.clearRecords();
                        view.addRecords( persons );
                    }
                });
    }

    @Inject
    Lang lang;

    @Inject
    AbstractContactTableView view;

    @Inject
    ContactServiceAsync contactService;

    private AppEvents.InitDetails initDetails;
}
