package ru.protei.portal.ui.crm.client.activity.app;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.common.PageService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.crm.client.widget.localeselector.LocaleImage;
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

        view.setUsername( event.profile.getName(), null );
        String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        view.locale().setValue( LocaleImage.findByLocale( currentLocale ));

        Scheduler.get().scheduleDeferred( (Command) () -> {
            if ( initialToken == null || initialToken.isEmpty() || initialToken.equals( UiConstants.LOGIN_PAGE ) ) {
                fireEvent( pageService.getFirstAvailablePageEvent() );
            } else {
                History.newItem( initialToken );
            }
        } );
    }

    @Override
    public void onLocaleChanged( String locale ) {
        changeLocale( locale );
    }

    public void onUserClicked() {
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

    @Inject
    AbstractAppView view;

    @Inject
    PageService pageService;

    String initialToken;
    private AppEvents.Init init;
}
