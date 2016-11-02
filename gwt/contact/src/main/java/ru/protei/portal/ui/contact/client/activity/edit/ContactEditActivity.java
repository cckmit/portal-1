package ru.protei.portal.ui.contact.client.activity.edit;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contact.client.service.ContactServiceAsync;

/**
 * Created by michael on 02.11.16.
 */
public abstract class ContactEditActivity implements AbstractContactEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( ContactEvents.Edit event ) {

        if(event.id == null) {
            this.fireEvent(new AppEvents.InitPanelName(lang.newContact()));
            setup(new Person());
        }
        else {
            this.fireEvent( new AppEvents.InitPanelName( "Edit contact with ID: " + event.id ) );
            contactService.getContact(event.id, new AsyncCallback<Person>() {
                @Override
                public void onFailure(Throwable throwable) {
                    fireEvent(new NotifyEvents.Show(lang.errorGetList(), NotifyEvents.NotifyType.ERROR));
                }

                @Override
                public void onSuccess(Person person) {
                    setup(person);
                }
            });
        }
    }

    @Override
    public void onSaveClicked() {
        fireEvent( new NotifyEvents.Show(lang.asteriskRequired(), NotifyEvents.NotifyType.ERROR));
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }


    private void setup (Person person){
        this.contact = person;
        initDetails.parent.clear();

        view.firstName().setText(person.getFirstName());
        view.lastName().setText(person.getLastName());
        view.secondName().setText(person.getSecondName());

        view.displayName().setText(person.getId() == null ? "Enter name to display" : person.getDisplayName());

        initDetails.parent.add(view.asWidget());
    }


    @Inject
    AbstractContactEditView view;

    @Inject
    Lang lang;

    Person contact;

    @Inject
    ContactServiceAsync contactService;


    private AppEvents.InitDetails initDetails;

}
