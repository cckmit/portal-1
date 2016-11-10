package ru.protei.portal.ui.crm.client.view.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppActivity;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppView;

/**
 * Вид основной формы приложения
 */
public class AppView extends Composite implements AbstractAppView, KeyUpHandler, ClickHandler {
    public AppView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initHandlers();
    }

    @Override
    public void setActivity( AbstractAppActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setUsername( String username, String role) {
        this.username.setInnerText( username );
        this.role.setInnerText( role );
    }

    @Override
    public void setPanelName(String panelName) {
//        this.panelName.setText( panelName );
    }

    @Override
    public HasWidgets getDetailsContainer() {
        return container;
    }

    @Override
    public HasWidgets getNotifyContainer() {
        return notifyContainer;
    }

    @UiHandler("searchButton")
    public void onSearchClicked(ClickEvent event) {
        event.preventDefault();
        if (search.getStyleName().contains("gl-search-input-open")) {
            search.removeStyleName("gl-search-input-open");
            search.setFocus(false);
            search.setText("");
            searchButton.removeStyleName("gl-close-button");
            searchButton.addStyleName("gl-search-button");
        } else {
            search.addStyleName("gl-search-input-open");
            search.setFocus(true);
            searchButton.removeStyleName("gl-search-button");
            searchButton.addStyleName("gl-close-button");
        }
     }

    @UiHandler("logout")
    public void onLogoutClicked(ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onLogoutClicked();
        }
    }

    @UiHandler("hideBarButton")
    public void onHideBarButtonClicked(ClickEvent event) {
        event.preventDefault();
        sidebar.addStyleName("inactive");
    }

    @UiHandler("companies")
    public void onCompaniesClicked(ClickEvent event) {
        event.preventDefault();
        clearTabs();
        companiesLi.addClassName( "active" );
        if ( activity != null ) {
            activity.onCompaniesClicked();
        }
    }

    @UiHandler("products")
    public void onProductsClicked(ClickEvent event) {
        event.preventDefault();
        clearTabs();
        productsLi.addClassName( "active" );
        if ( activity != null ) {
            activity.onProductsClicked();
        }
    }

    @UiHandler("contacts")
    public void onContactsClicked(ClickEvent event) {
        event.preventDefault();
        clearTabs();
        contactsLi.addClassName( "active" );
        if ( activity != null ) {
            activity.onContactsClicked();
        }
    }

    @Override
    public void onKeyUp (KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_F4 && event.isAnyModifierKeyDown() && event.isControlKeyDown()) {
            event.preventDefault();
            activity.onLogoutClicked();
        }
    }

    @Override
    public void onClick (ClickEvent event) {
        sidebar.removeStyleName("inactive");
    }

    private void initHandlers() {
        RootPanel.get().sinkEvents(Event.ONKEYUP);
        RootPanel.get().addHandler(this, KeyUpEvent.getType());
    }

    public void setFocus () {
        search.setFocus(true);
    }

    private void clearTabs() {
        companiesLi.removeClassName( "active" );
        contactsLi.removeClassName( "active" );
        productsLi.removeClassName( "active" );
    }

    @UiField
    HTMLPanel appPanel;

    @UiField
    Anchor hideBarButton;

    @UiField
    TextBox search;
    @UiField
    Anchor logout;

    @UiField
    HTMLPanel sidebar;
    @UiField
    HTMLPanel container;

//    @UiField
//    Label panelName;


    @UiField
    Anchor companies;
    @UiField
    Anchor products;
    @UiField
    Anchor contacts;

    @UiField
    HTMLPanel notifyContainer;
    @UiField
    Anchor searchButton;
    @UiField
    LIElement productsLi;
    @UiField
    LIElement companiesLi;
    @UiField
    LIElement contactsLi;
    @UiField
    ParagraphElement username;
    @UiField
    AnchorElement role;

    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder<Widget, AppView> {}
    private static  AppViewUiBinder ourUiBinder = GWT.create(  AppViewUiBinder.class );
}