package ru.protei.portal.ui.crm.client.activity.app;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.crm.client.events.AppEvents;
import ru.protei.portal.ui.crm.client.events.AuthEvents;
import ru.protei.portal.ui.crm.client.events.CompanyEvents;

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
        view.setUsername(event.userName);
    }

    @Event
    public void onShowApp( AppEvents.Show event ) {

        init.parent.clear();
        init.parent.add(view.asWidget());

        view.setPanelName("CRM common page");
        view.setFocus();

        fireEvent(new AppEvents.InitDetails(view.getDetailsContainer()));
    }

    @Event
    public void onInitPanelName(AppEvents.InitPanelName event) {
        view.setPanelName (event.panelName);
    }

    public void onUserClicked() {
        Window.alert("Wow! User clicked!");
    }

    public void onLogoutClicked() {

        init.parent.clear();

        fireEvent( new AuthEvents.Show ( init.parent ));
    }

    @Override
    public void onCompaniesClicked() {
        fireEvent( new CompanyEvents.Show ( ));
    }

    @Inject
    AbstractAppView view;
}
