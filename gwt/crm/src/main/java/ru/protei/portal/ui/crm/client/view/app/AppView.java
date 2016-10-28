package ru.protei.portal.ui.crm.client.view.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
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
    public void setUsername(String username) {
        this.user.setText( username );
    }

    @Override
    public void setPanelName(String panelName) {
        this.panelName.setText( panelName );
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

    @UiHandler("user")
    public void onUserClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onUserClicked();
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
        logo.addClassName("inactive");
        sidebar.addStyleName("inactive");
    }

    @UiHandler("companies")
    public void onCompaniesClicked(ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onCompaniesClicked();
        }
    }

    @UiHandler("products")
    public void onProductsClicked(ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onProductsClicked();
        }
    }

    @UiHandler("companies")
    public void onContactsClicked(ClickEvent event) {
        event.preventDefault();
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
        logo.removeClassName("inactive");
        sidebar.removeStyleName("inactive");
    }

    private void initHandlers() {

        noScrol.sinkEvents(Event.ONCLICK);
        noScrol.addHandler(this, ClickEvent.getType());

        RootPanel.get().sinkEvents(Event.ONKEYUP);
        RootPanel.get().addHandler(this, KeyUpEvent.getType());
    }

    public void setFocus () {
        search.setFocus(true);
    }

    @UiField
    HTMLPanel appPanel;

    @UiField
    DivElement logo;
    @UiField
    Anchor hideBarButton;

    @UiField
    HTMLPanel noScrol;

    @UiField
    HTMLPanel navbar;
    @UiField
    TextBox search;
    @UiField
    Anchor logout;

    @UiField
    HTMLPanel sidebar;
    @UiField
    HTMLPanel container;

    @UiField
    Anchor user;
    @UiField
    Label panelName;


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

    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder<Widget, AppView> {}
    private static  AppViewUiBinder ourUiBinder = GWT.create(  AppViewUiBinder.class );
}