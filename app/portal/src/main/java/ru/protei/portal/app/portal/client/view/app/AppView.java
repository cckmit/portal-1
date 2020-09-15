package ru.protei.portal.app.portal.client.view.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.app.portal.client.activity.app.AbstractAppActivity;
import ru.protei.portal.app.portal.client.activity.app.AbstractAppView;
import ru.protei.portal.app.portal.client.widget.locale.LocaleImage;
import ru.protei.portal.app.portal.client.widget.locale.LocaleSelector;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

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
        fixOpenedSidebarButton.getElement().setAttribute("data-toggle-pin", "sidebar");
        fixClosedSidebarButton.getElement().setAttribute("data-toggle-pin", "sidebar");
        isFixedOpened = Boolean.parseBoolean( localStorageService.getOrDefault( "fixed-opened-sidebar", "false" ));
        isFixedClosed = Boolean.parseBoolean( localStorageService.getOrDefault( "fixed-closed-sidebar", "false" ));
        if (isFixedOpened){
            fixOpenedSidebar(true);
            fixOpenedSidebarButton.removeStyleName("hide");
            closedSidebarControlsContainer.addStyleName("hide");
        }
        if (isFixedClosed) {
            fixClosedSidebar(true);
        }
    }

    @Override
    public void setActivity( AbstractAppActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setUser(String name, String company, String photoSrc) {
        username.setInnerText(name);
        photo.setSrc(photoSrc);
    }

    @Override
    public void setLogoByLocale(String locale) {
        logoImgWhite.setSrc("./images/logo-white-" + locale + ".svg");
        logoImgBlue.setSrc("./images/logo-blue-" + locale + ".svg");
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

    @Override
    public void setExternalLinks(String htmlString) {

        HTML html = new HTML(htmlString);

        menuContainer.getElement().setInnerHTML(menuContainer.getElement().getInnerHTML() + html.getHTML());

        NodeList<Element> anchors = html.getElement().getElementsByTagName("a");
        for (int i = 0; i < anchors.getLength(); i++) {
            if (anchors.getItem(i).getPropertyString("href").endsWith("#")) {
                Element anchor = Document.get().getElementById(anchors.getItem(i).getId());
                Element submenu = Document.get().getElementById(anchor.getParentElement().getElementsByTagName("ul").getItem(0).getId());
                addOnAnchorClickListener(anchor, submenu);
            }
        }
    }

    private native void addOnAnchorClickListener(Element anchor, Element submenu) /*-{
        anchor.addEventListener("click", function (event) {
            event.preventDefault();
            event.stopPropagation();
            var arrow = anchor.getElementsByClassName("arrow").item(0);
            var opened = arrow.classList.contains("open");
            var height = submenu.childElementCount * 38 + 30;
            console.log(submenu.firstChild.height);
            if (opened) {
                arrow.classList.remove("open");
                submenu.style.cssText = 'margin:0px;padding:0;height:0;';
            } else {
                arrow.classList.add("open");
                submenu.style.cssText = 'margin:0px;padding-top:18px;padding-bottom:10px;margin-bottom:10px;height:' + height + 'px';
            }
        })
    }-*/;


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

    @UiHandler({"profile", "menuBarFocus"})
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

    @UiHandler("fixOpenedSidebarButton")
    public void onOpenedClicked(ClickEvent event){
        fixOpenedSidebar(!isFixedOpened);
    }

    @UiHandler("fixClosedSidebarButton")
    public void onClosedClicked(ClickEvent event){
        fixClosedSidebar(!isFixedClosed);
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
        fixClosedSidebarButton.ensureDebugId(DebugIds.APP_VIEW.FIX_CLOSED_SIDEBAR_BUTTON);
        fixOpenedSidebarButton.ensureDebugId(DebugIds.APP_VIEW.FIX_OPENED_SIDEBAR_BUTTON);
    }

    private void initHandlers() {
        RootPanel.get().sinkEvents( Event.ONKEYUP );
        RootPanel.get().addHandler( this, KeyUpEvent.getType() );

        closedSidebarControlsContainer.sinkEvents( Event.ONMOUSEOVER );
        closedSidebarControlsContainer.addHandler(DomEvent::stopPropagation, MouseOverEvent.getType());

        closedSidebarControlsContainer.sinkEvents( Event.ONMOUSEOUT );
        closedSidebarControlsContainer.addHandler(DomEvent::stopPropagation, MouseOutEvent.getType());

        navbar.sinkEvents( Event.ONMOUSEOVER );
        navbar.addHandler( event -> {
            if (!isFixedOpened && !isFixedClosed) {
                RootPanel.get().addStyleName("sidebar-visible");
                navbar.getElement().getStyle().setProperty("transform", "translate(200px, 0px)");
                fixOpenedSidebarButton.removeStyleName("hide");
                closedSidebarControlsContainer.addStyleName("hide");
            }
        }, MouseOverEvent.getType() );

        navbar.sinkEvents( Event.ONMOUSEOUT );
        navbar.addHandler( event -> {
            if (!isFixedOpened && !isFixedClosed) {
                RootPanel.get().removeStyleName("sidebar-visible");
                navbar.getElement().getStyle().setProperty("transform", "translate3d(0px, 0px, 0px)");
                fixOpenedSidebarButton.addStyleName("hide");
                closedSidebarControlsContainer.removeStyleName("hide");
            }
        }, MouseOutEvent.getType() );
    }

    private void fixOpenedSidebar(boolean fix){
        isFixedOpened = fix;
        localStorageService.set( "fixed-opened-sidebar", String.valueOf(fix));
        RootPanel.get().setStyleName("menu-pin", fix);
        actionBarContainer.setStyleName("p-l-30", !fix);
        actionBarContainer.setStyleName("p-l-40", fix);
        fixOpenedSidebarButton.setStyleName("fixed-sidebar", fix);
    }

    private void fixClosedSidebar(boolean fix){
        isFixedClosed = fix;
        localStorageService.set( "fixed-closed-sidebar", String.valueOf(fix));
        fixClosedSidebarButton.setStyleName("fixed-sidebar", fix);
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
    Button fixOpenedSidebarButton;
    @UiField
    Button fixClosedSidebarButton;
    @Inject
    @UiField(provided = true)
    LocaleSelector locale;
    @Inject
    LocalStorageService localStorageService;
    @UiField
    DivElement headerDiv;
    @UiField
    DivElement brandDiv;
    @UiField
    DivElement openedSidebarControlsContainer;
    @UiField
    HTMLPanel closedSidebarControlsContainer;
    @UiField
    ImageElement logoImgWhite;
    @UiField
    ImageElement logoImgBlue;


    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder< Widget, AppView > {}

    private boolean isFixedOpened = false;
    private boolean isFixedClosed = false;

    private static AppViewUiBinder ourUiBinder = GWT.create( AppViewUiBinder.class );
}