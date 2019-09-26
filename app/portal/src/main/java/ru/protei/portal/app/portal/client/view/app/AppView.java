package ru.protei.portal.app.portal.client.view.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
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
import ru.protei.portal.app.portal.client.activity.app.AbstractAppActivity;
import ru.protei.portal.app.portal.client.activity.app.AbstractAppView;
import ru.protei.portal.app.portal.client.widget.locale.LocaleImage;
import ru.protei.portal.app.portal.client.widget.locale.LocaleSelector;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.LocalStorageService;

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
        fixSidebarButton.getElement().setAttribute("data-toggle-pin", "sidebar");
        boolean isFixed = Boolean.parseBoolean( localStorageService.getOrDefault( "fixed-sidebar", "false" ));
        if (isFixed){
            fixSidebar();
            fixSidebarButton.removeStyleName("hide");
        }
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
        boolean isOpened = RootPanel.get().getStyleName().contains( "sidebar-open" );
        if ( isOpened ) {
            closeSidebar();
            return;
        }
        openSidebar();
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

    @UiHandler("fixSidebarButton")
    public void onChecked (ClickEvent event){

        if (!fixSidebarButton.getStyleName().contains("fixed-sidebar")){
            fixSidebar();
        }
        else {
            unfixSidebar();
        }
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
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        globalContainer.ensureDebugId(DebugIds.APP_VIEW.GLOBAL_CONTAINER);
        logout.ensureDebugId(DebugIds.APP_VIEW.LOGOUT_BUTTON);
        toggleButton.ensureDebugId(DebugIds.APP_VIEW.TOGGLE_SIDEBAR_BUTTON);
        profile.ensureDebugId(DebugIds.APP_VIEW.USER_PANEL);
        notifyContainer.ensureDebugId(DebugIds.APP_VIEW.NOTIFICATION_CONTAINER);
        locale.ensureDebugId(DebugIds.APP_VIEW.LOCALE_SELECTOR);
        settings.ensureDebugId(DebugIds.APP_VIEW.SETTING_BUTTON);
        username.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.APP_VIEW.USER_NAME_LABEL);
        navbar.ensureDebugId(DebugIds.APP_VIEW.SIDEBAR);
        logo.ensureDebugId(DebugIds.APP_VIEW.DASHBOARD_BUTTON);
    }

    private void initHandlers() {
        RootPanel.get().sinkEvents( Event.ONKEYUP );
        RootPanel.get().addHandler( this, KeyUpEvent.getType() );

        navbar.sinkEvents( Event.ONMOUSEOVER );
        navbar.addHandler( event -> {
            RootPanel.get().addStyleName("sidebar-visible");
            navbar.getElement().getStyle().setProperty("transform", "translate(200px, 0px)");
            fixSidebarButton.removeStyleName("hide");
        }, MouseOverEvent.getType() );

        navbar.sinkEvents( Event.ONMOUSEOUT );
        navbar.addHandler( event -> {
            RootPanel.get().removeStyleName("sidebar-visible");
            navbar.getElement().getStyle().setProperty("transform", "translate3d(0px, 0px, 0px)");
            if (!fixSidebarButton.getStyleName().contains("fixed-sidebar")) fixSidebarButton.addStyleName("hide");
        }, MouseOutEvent.getType() );
    }

    private void fixSidebar(){
        localStorageService.set( "fixed-sidebar", "true");
        RootPanel.get().addStyleName("menu-pin");
        actionBarContainer.removeStyleName("p-l-30");
        actionBarContainer.addStyleName("p-l-40");
        fixSidebarButton.addStyleName("fixed-sidebar");
    }

    private void unfixSidebar(){
        localStorageService.set( "fixed-sidebar", "false");
        RootPanel.get().removeStyleName("menu-pin");
        actionBarContainer.removeStyleName("p-l-40");
        actionBarContainer.addStyleName("p-l-30");
        fixSidebarButton.removeStyleName("fixed-sidebar");
    }

    private void openSidebar(){
        RootPanel.get().addStyleName( "sidebar-open" );
        navbar.addStyleName("visible");
        headerDiv.addClassName("header-padding");
        brandDiv.addClassName("hide");
    }

    private void closeSidebar(){
        RootPanel.get().removeStyleName( "sidebar-open" );
        navbar.removeStyleName("visible");
        headerDiv.removeClassName("header-padding");
        brandDiv.removeClassName("hide");
    }

    @UiField
    Anchor toggleButton;
    @UiField
    Anchor logout;

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
    @UiField
    Button fixSidebarButton;
    @Inject
    @UiField(provided = true)
    LocaleSelector locale;
    @Inject
    LocalStorageService localStorageService;
    @UiField
    DivElement headerDiv;
    @UiField
    DivElement brandDiv;


    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder< Widget, AppView > {}

    private static AppViewUiBinder ourUiBinder = GWT.create( AppViewUiBinder.class );
}