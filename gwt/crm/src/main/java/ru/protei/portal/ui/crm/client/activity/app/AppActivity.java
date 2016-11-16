package ru.protei.portal.ui.crm.client.activity.app;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.winter.web.common.client.events.MenuEvents;

/**
 * Активность приложения
 */
public abstract class AppActivity
        implements Activity, AbstractAppActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);

        fireEvent( new MenuEvents.Init( view.getMenuContainer() ) );
    }

    @Event
    public void onInit(AppEvents.Init event) {
        this.init = event;

        fireEvent(new AppEvents.InitDetails(view.getDetailsContainer()));
        fireEvent(new NotifyEvents.Init(view.getNotifyContainer()));

        // запомнить токен
        initialToken = History.getToken();

        fireEvent( new AuthEvents.Show() );
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {

        init.parent.clear();
        init.parent.add( view.asWidget() );

        view.setUsername( event.profile.getName(), event.profile.getRole().getCaRoleName() );

        if ( initialToken.isEmpty() ) {
            fireEvent( new AppEvents.Show() );
            return;
        }

        if (initialToken.equals( History.getToken() )) {
            fireEvent( new AppEvents.Show() );
            return;
        }

        History.newItem( initialToken );
    }

    /**
     *  Здесь дыра!
     *  Можно войти без авторизации и получить доступ ко всем сущностям
     */

/*    @Event
    public void onShowApp( AppEvents.Show event ) {
        init.parent.clear();
        init.parent.add(view.asWidget());
    }*/

    @Event
    public void onInitPanelName(AppEvents.InitPanelName event) {}

    public void onUserClicked() {
        Window.alert("Wow! User clicked!");
    }

    public void onLogoutClicked() {
        fireEvent( new AppEvents.Logout() );
        view.getDetailsContainer().clear();
    }

    private void initApp() {
        fireEvent( new AppEvents.InitDetails( view.getDetailsContainer() ) );
        fireEvent( new NotifyEvents.Init(view.getNotifyContainer()) );
    }

    @Inject
    AbstractAppView view;

    String initialToken;
    private AppEvents.Init init;
}
