package ru.protei.portal.app.portal.client.activity.app;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.service.AppServiceAsync;
import ru.protei.portal.app.portal.client.widget.locale.LocaleImage;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.ConfigStorage;
import ru.protei.portal.ui.common.client.common.PageService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.client.service.PingControllerAsync;
import ru.protei.portal.ui.common.client.util.LocaleUtils;
import ru.protei.portal.ui.common.shared.model.ClientConfigData;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.web.common.client.events.MenuEvents;

import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliteration;


/**
 * Активность приложения
 */
public abstract class AppActivity
        implements Activity, AbstractAppActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        requestsClientConfigAndSetAppVersion();
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

        view.setUser(transliteration(event.profile.getShortName()),
                event.profile.getCompany() == null ? "" : transliteration(event.profile.getCompany().getCname()),
                AvatarUtils.getAvatarUrl(event.profile));

        String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        view.locale().setValue(LocaleImage.findByLocale(currentLocale));
        view.setLogoByLocale(currentLocale);

        Scheduler.get().scheduleDeferred( (Command) () -> {
            if ( initialToken == null || initialToken.isEmpty() || initialToken.equals( UiConstants.LOGIN_PAGE ) ) {
                fireEvent( pageService.getFirstAvailablePageEvent() );
            } else {
                History.newItem( initialToken );
            }
        } );

        startPingServerTimer();
        view.showHelp(policyService.personBelongsToHomeCompany());
    }

    @Event
    public void onInitExternalLinks(AppEvents.InitExternalLinks event) {
        view.setExternalLinks(event.ExternalLinksHtml);
    }

    @Event
    public void onMenuClear(MenuEvents.Clear event) {
        view.clearExternalLinks();
    }

    @Override
    public void onLogoClicked() {
        fireEvent(pageService.getFirstAvailablePageEvent());
    }

    @Override
    public void onLocaleChanged(String locale) {
        LocaleUtils.changeLocale( locale );
    }

    @Override
    public void onSettingsClicked() {
        fireEvent( new AppEvents.ShowProfile());
    }

    @Override
    public void onMenuSectionsClose() {
        fireEvent(new MenuEvents.CloseAll());
    }

    public void onLogoutClicked() {
        initialToken = null;
        fireEvent( new AppEvents.Logout() );
        fireEvent( new MenuEvents.Clear() );

        view.getDetailsContainer().clear();
    }

    private void initApp() {
        fireEvent( new MenuEvents.Init( view.getMenuContainer() ) );
        fireEvent( new AppEvents.InitDetails( view.getDetailsContainer() ) );
        fireEvent( new NotifyEvents.Init( view.getNotifyContainer() ) );
        fireEvent( new ActionBarEvents.Init( view.getActionBarContainer() ) );
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

    private void requestsClientConfigAndSetAppVersion() {
        appService.getClientConfig(new FluentCallback<ClientConfigData>()
                .withSuccess(config -> {
                    if (config != null) {
                        configStorage.setConfigData(config);
                        view.setAppVersion(lang.version() + " " + configStorage.getConfigData().appVersion);
                    }
                }));
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
    AppServiceAsync appService;
    @Inject
    Lang lang;
    @Inject
    ConfigStorage configStorage;
    @Inject
    PolicyService policyService;

    String initialToken;
    private AppEvents.Init init;

    private Timer pingTimer = new Timer() {
        @Override
        public void run() {
            pingServer();
        }
    };
}
