package ru.protei.portal.app.portal.client.view.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.app.portal.client.widget.locale.LocaleImage;
import ru.protei.portal.app.portal.client.widget.locale.LocaleSelector;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.app.portal.client.activity.app.AbstractAppActivity;
import ru.protei.portal.app.portal.client.activity.app.AbstractAppView;

/**
 * Вид основной формы приложения
 */
public class AppView extends Composite
        implements AbstractAppView,
        KeyUpHandler {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
        initHandlers();
    }

    @Override
    public void setActivity( AbstractAppActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setUser( String name, String company, String photoSrc) {
        username.setInnerText(name);
        photo.setSrc(photoSrc);
    }

    @Override
    public void setAppVersion(String appVersion) {
        this.appVersion.setText(appVersion);
    }

    @Override
    public HasWidgets getDetailsContainer() {
        return container;
    }

    @Override
    public HasWidgets getMenuContainer() {
        return menuContainer;
    }

    @Override
    public HasWidgets getNotifyContainer() {
        return notifyContainer;
    }

    @Override
    public HasWidgets getActionBarContainer() {
        return actionBarContainer;
    }

    @Override
    public HasValue<LocaleImage> locale() {
        return locale;
    }

    @UiHandler( "logout" )
    public void onLogoutClicked( ClickEvent event ) {
        event.preventDefault();
        menuBar.removeStyleName("show");

        if ( activity != null ) {
            activity.onLogoutClicked();
        }
    }

    @UiHandler( "toggleButton" )
    public void onToggleButtonClicked( ClickEvent event ) {
        event.preventDefault();
        boolean isCollapsed = RootPanel.get().getStyleName().contains( "sidebar-collapse" );
        if ( isCollapsed ) {
            RootPanel.get().removeStyleName( "sidebar-collapse" );
            return;
        }

        RootPanel.get().addStyleName( "sidebar-collapse" );
    }

    @UiHandler("logo")
    public void logoClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onLogoClicked();
        }
    }

    @UiHandler("settings")
    public void settingsClick(ClickEvent event) {
        event.preventDefault();
        menuBar.removeStyleName("show");

        if (activity != null) {
            activity.onSettingsClicked();
        }
    }

    @UiHandler("profile")
    public void profileClick(ClickEvent event) {
        if (menuBar.getStyleName().contains("show")) {
            menuBar.removeStyleName( "show" );
        } else {
            menuBar.addStyleName( "show" );
        }
    }

    @UiHandler("menuBarFocus")
    public void profileClick(MouseOutEvent event) {
        menuBar.removeStyleName("show");
    }


    @UiHandler("locale")
    public void onLocaleChanged(ValueChangeEvent<LocaleImage> event) {
        activity.onLocaleChanged(event.getValue().getLocale());
    }

    @Override
    public void onKeyUp( KeyUpEvent event ) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE && event.isControlKeyDown()) {
            event.preventDefault();
            activity.onLogoutClicked();
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        menuBar.removeStyleName("show");
    }

    private void ensureDebugIds() {
        globalContainer.ensureDebugId(DebugIds.APP_VIEW.GLOBAL_CONTAINER);
        logout.ensureDebugId(DebugIds.APP_VIEW.LOGOUT_BUTTON);
        toggleButton.ensureDebugId(DebugIds.APP_VIEW.TOGGLE_SIDEBAR_BUTTON);
        profile.ensureDebugId(DebugIds.APP_VIEW.USER_PANEL);
        notifyContainer.ensureDebugId(DebugIds.APP_VIEW.NOTIFICATION_CONTAINER);
        locale.ensureDebugId(DebugIds.APP_VIEW.LOCALE_SELECTOR);
    }

    private void initHandlers() {
        RootPanel.get().sinkEvents( Event.ONKEYUP );
        RootPanel.get().addHandler( this, KeyUpEvent.getType() );

        navbar.sinkEvents( Event.ONMOUSEOVER );
        navbar.addHandler( event -> {
            RootPanel.get().addStyleName("sidebar-visible");
            navbar.getElement().getStyle().setProperty("transform", "translate(210px, 0px)");
        }, MouseOverEvent.getType() );

        navbar.sinkEvents( Event.ONMOUSEOUT );
        navbar.addHandler( event -> {
            RootPanel.get().removeStyleName("sidebar-visible");
            navbar.getElement().getStyle().setProperty("transform", "translate3d(0px, 0px, 0px)");
        }, MouseOutEvent.getType() );
    }

    @UiField
    Anchor toggleButton;
    @UiField
    Anchor logout;

    @UiField
    HTMLPanel sidebar;
    @UiField
    HTMLPanel container;

    @UiField
    HTMLPanel notifyContainer;
    @UiField
    HTMLPanel menuContainer;
    @UiField
    HTMLPanel actionBarContainer;
    @UiField
    Anchor logo;
    @UiField
    Label appVersion;
    @UiField
    HTMLPanel globalContainer;
    @UiField
    SpanElement username;
    @UiField
    HTMLPanel navbar;
    @UiField
    ImageElement photo;
    @UiField
    Anchor settings;
    @UiField
    FocusPanel menuBarFocus;
    @UiField
    HTMLPanel menuBar;
    @UiField
    Button profile;
    @Inject
    @UiField(provided = true)
    LocaleSelector locale;

    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder< Widget, AppView > {}

    private static AppViewUiBinder ourUiBinder = GWT.create( AppViewUiBinder.class );
}