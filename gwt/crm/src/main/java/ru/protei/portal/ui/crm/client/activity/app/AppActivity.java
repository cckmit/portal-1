package ru.protei.portal.ui.crm.client.activity.app;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.events.*;

/**
 * Активность приложения
 */
public abstract class AppActivity
        implements Activity, AbstractAppActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInit(AppEvents.Init event) {
        this.init = event;

        fireEvent(new AppEvents.InitDetails(view.getDetailsContainer()));
        fireEvent(new NotifyEvents.Init(view.getNotifyContainer()));
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        view.setUsername( event.profile.getName(), event.profile.getRole().getCaRoleName() );
    }

    @Event
    public void onShowApp( AppEvents.Show event ) {
        init.parent.clear();
        init.parent.add(view.asWidget());
    }

    @Event
    public void onInitPanelName(AppEvents.InitPanelName event) {
        view.setPanelName(event.panelName);
    }

    public void onUserClicked() {
        Window.alert("Wow! User clicked!");
    }

    public void onLogoutClicked() {
        fireEvent(new AppEvents.Logout());
    }

    @Override
    public void onCompaniesClicked() {
        fireEvent( new CompanyEvents.Show ( ));
    }

    @Override
    public void onProductsClicked() {
        fireEvent( new ProductEvents.Show ( ));
    }

    @Override
    public void onContactsClicked() {
        fireEvent( new ContactEvents.Show ( ));
    }

    @Inject
    AbstractAppView view;

    private AppEvents.Init init;
}
