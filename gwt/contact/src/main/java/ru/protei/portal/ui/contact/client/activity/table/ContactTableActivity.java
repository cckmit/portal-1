package ru.protei.portal.ui.contact.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Created by turik on 28.10.16.
 */
public abstract class ContactTableActivity implements AbstractContactTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( ContactEvents.Show event ) {

        this.fireEvent( new AppEvents.InitPanelName( lang.contacts() ) );
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        //view.getChildContainer().clear();
        //initContacts();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Inject
    Lang lang;

    @Inject
    AbstractContactTableView view;

    private AppEvents.InitDetails initDetails;
}
