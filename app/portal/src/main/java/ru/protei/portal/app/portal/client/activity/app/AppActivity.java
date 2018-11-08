package ru.protei.portal.app.portal.client.activity.app;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.app.portal.client.widget.localeselector.LocaleImage;
import ru.protei.portal.ui.common.client.common.PageService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.common.UserIconUtils;
import ru.protei.portal.ui.common.client.common.Version;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PingControllerAsync;
import ru.protei.winter.web.common.client.events.MenuEvents;

/**
 * Активность приложения
 */
public abstract class AppActivity
        implements Activity, AbstractAppActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );

        fireEvent( new MenuEvents.Init( view.getMenuContainer() ) );
    }

    @Event
    public void onInit( AppEvents.Init event ) {
        this.init = event;

        initApp();

        initialToken = History.getToken();
        fireEvent( new AuthEvents.Show() );
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        init.parent.clear();
        init.parent.add( view.asWidget() );

        view.setUser( event.profile.getName(), event.profile.getCompany() == null ? "" : event.profile.getCompany().getCname(), UserIconUtils.getGenderIcon(event.profile.getGender()));

        view.setAppVersion(lang.version() + " " + Version.getVersion());

        String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        view.locale().setValue( LocaleImage.findByLocale( currentLocale ));

        Scheduler.get().scheduleDeferred( (Command) () -> {
            if ( initialToken == null || initialToken.isEmpty() || initialToken.equals( UiConstants.LOGIN_PAGE ) ) {
                fireEvent( pageService.getFirstAvailablePageEvent() );
            } else {
                History.newItem( initialToken );
            }
        } );

        startPingServerTimer();
    }

    @Override
    public void onLocaleChanged( String locale ) {
        changeLocale( locale );
    }

    @Override
    public void onLogoClicked() {
        fireEvent(pageService.getFirstAvailablePageEvent());
    }

    public void onUserClicked() {
        fireEvent( new AppEvents.ShowProfile());
    }

    public void onLogoutClicked() {
        initialToken = null;
        fireEvent( new AppEvents.Logout() );
        fireEvent( new MenuEvents.Clear() );

        view.getDetailsContainer().clear();
    }

    private void initApp() {
        fireEvent( new AppEvents.InitDetails( view.getDetailsContainer() ) );
        fireEvent( new NotifyEvents.Init( view.getNotifyContainer() ) );
        fireEvent( new ActionBarEvents.Init( view.getActionBarContainer() ) );
    }

    private void changeLocale( String language ) {
        String href = Window.Location.getHref();
        String newHref;

        if ( !href.contains( "locale" ) ) {
            newHref = href.contains( "#" ) ? href.replace( "#", "?locale=" + language + "#" ) : href + "?locale=" + language;
        } else {
            String locale = LocaleInfo.getCurrentLocale().getLocaleName();
            if ( locale.isEmpty() )
                newHref = href.replace( "#", "?locale=" + language + "#" );
            else {
                String replace = "locale=" + locale;
                newHref = href.replace( replace, "locale=" + language );
            }
        }
        Window.Location.replace( newHref );
    }

    private void pingServer() {
        pingService.ping( new AsyncCallback<Void>() {
            @Override
            public void onFailure( Throwable caught ) {
                stopPingServerTimer();
                fireEvent(new AuthEvents.Show());
            }
            @Override
            public void onSuccess( Void result ) {}
        } );
    }

    private void startPingServerTimer() {
        stopPingServerTimer();
        pingTimer.scheduleRepeating( 60000 );
    }

    private void stopPingServerTimer() {
        pingTimer.cancel();
    }

    @Inject
    AbstractAppView view;
    @Inject
    PageService pageService;
    @Inject
    PingControllerAsync pingService;
    @Inject
    Lang lang;

    String initialToken;
    private AppEvents.Init init;

    private Timer pingTimer = new Timer() {
        @Override
        public void run() {
            pingServer();
        }
    };
}
