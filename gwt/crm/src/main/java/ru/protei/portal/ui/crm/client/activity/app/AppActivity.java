package ru.protei.portal.ui.crm.client.activity.app;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.crm.client.events.AppEvents;
import ru.protei.portal.ui.crm.client.events.AuthEvents;

/**
 * Активность приложения
 */
public abstract class AppActivity
        implements Activity, AbstractAppActivity {

    private AppEvents.Init init;

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit(AppEvents.Init event) { this.init = event; }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        view.setUsername( " " + event.userName );

        fireEvent(new AppEvents.Show());
    }

    @Event
    public void onShowApp( AppEvents.Show event ) {

        init.parent.clear();
        init.parent.add(view.asWidget());

        view.setPanelName("CRM common page");

        fireEvent( new AppEvents.InitDetails( view.getDetailsContainer() ));
    }

    @Override
    public void onUserClicked() {
        Window.alert("Wow! User clicked!");
    }

    public void onLogoutClicked() {

        Window.alert("Logout!");
        init.parent.clear();

        fireEvent( new AuthEvents.Show ( init.parent ));
    }

    @Inject
    AbstractAppView view;
}
